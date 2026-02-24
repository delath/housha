package dev.delath.housha.ui.all

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.delath.housha.databinding.ItemAnimeBinding
import dev.delath.housha.model.Anime

class AnimeAdapter(
    private val onSubscribeClick: (Anime, Boolean) -> Unit
) : ListAdapter<Anime, AnimeAdapter.AnimeViewHolder>(AnimeDiffCallback()) {

    private var subscribedIds: Set<Int> = emptySet()

    fun setSubscribedIds(ids: Set<Int>) {
        val previous = subscribedIds
        subscribedIds = ids
        // Notify only items whose subscribed state actually changed, with a payload
        // so RecyclerView skips the cross-fade animation and only redraws the button.
        for (i in 0 until itemCount) {
            val id = getItem(i).id
            if (previous.contains(id) != ids.contains(id)) {
                notifyItemChanged(i, PAYLOAD_SUBSCRIBED_CHANGED)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, subscribedIds.contains(item.id), onSubscribeClick)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.contains(PAYLOAD_SUBSCRIBED_CHANGED)) {
            val item = getItem(position)
            holder.bindButton(item, subscribedIds.contains(item.id), onSubscribeClick)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    class AnimeViewHolder(private val binding: ItemAnimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(anime: Anime, isSubscribed: Boolean, onSubscribeClick: (Anime, Boolean) -> Unit) {
            binding.textViewAnimeTitle.text = anime.title

            binding.textViewStudio.text = anime.studio
            binding.textViewStudio.visibility = if (!anime.studio.isNullOrBlank()) View.VISIBLE else View.GONE

            val score = anime.averageScore
            if (score != null && score > 0) {
                binding.textViewScore.text = "★ $score"
                binding.textViewScore.visibility = View.VISIBLE
            } else {
                binding.textViewScore.visibility = View.GONE
            }

            binding.textViewEpisodes.text = when {
                anime.episodeCount == 1      -> "1 episode"
                anime.episodeCount > 1       -> "${anime.episodeCount} episodes"
                anime.status == "RELEASING"  -> "Ongoing"
                else                         -> "TBA"
            }

            val nextEpNum = anime.nextEpisodeNumber
            val nextEpAt  = anime.nextEpisodeAt
            if (nextEpNum != null && nextEpAt != null) {
                val secondsLeft = nextEpAt - System.currentTimeMillis() / 1000
                binding.textViewAiring.text = if (secondsLeft > 0) {
                    "Ep $nextEpNum in ${formatCountdown(secondsLeft)}"
                } else {
                    "Ep $nextEpNum airing now"
                }
                binding.textViewAiring.visibility = View.VISIBLE
            } else {
                binding.textViewAiring.visibility = View.GONE
            }

            Glide.with(binding.imageViewAnimeCover.context)
                .load(anime.imageUrl)
                .into(binding.imageViewAnimeCover)

            bindButton(anime, isSubscribed, onSubscribeClick)
        }

        fun bindButton(anime: Anime, isSubscribed: Boolean, onSubscribeClick: (Anime, Boolean) -> Unit) {
            val iconRes  = if (isSubscribed) android.R.drawable.ic_delete else android.R.drawable.ic_input_add
            val tintAttr = if (isSubscribed) android.R.attr.colorError else android.R.attr.colorPrimary
            val typedArray = binding.root.context.obtainStyledAttributes(intArrayOf(tintAttr))
            val tintColor = typedArray.getColor(0, 0)
            typedArray.recycle()
            binding.buttonSubscribe.setImageResource(iconRes)
            binding.buttonSubscribe.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
            binding.buttonSubscribe.setOnClickListener { onSubscribeClick(anime, isSubscribed) }
        }

        private fun formatCountdown(seconds: Long): String {
            val days    = seconds / 86400
            val hours   = (seconds % 86400) / 3600
            val minutes = (seconds % 3600) / 60
            return when {
                days > 0  -> "${days}d ${hours}h"
                hours > 0 -> "${hours}h ${minutes}m"
                else      -> "${minutes}m"
            }
        }
    }

    class AnimeDiffCallback : DiffUtil.ItemCallback<Anime>() {
        override fun areItemsTheSame(oldItem: Anime, newItem: Anime) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Anime, newItem: Anime) = oldItem == newItem
    }

    companion object {
        private const val PAYLOAD_SUBSCRIBED_CHANGED = "subscribed_changed"
    }
}
