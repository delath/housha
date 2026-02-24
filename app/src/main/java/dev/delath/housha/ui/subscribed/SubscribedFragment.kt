package dev.delath.housha.ui.subscribed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.delath.housha.R
import dev.delath.housha.databinding.FragmentSubscribedBinding
import dev.delath.housha.ui.all.AnimeAdapter
import dev.delath.housha.util.applySystemBarInsets
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubscribedFragment : Fragment() {

    private var _binding: FragmentSubscribedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SubscribedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscribedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applySystemBarInsets(
            binding.rootLayout,
            binding.recyclerViewSubscribed,
            bottomNavView = requireActivity().findViewById(R.id.bottom_nav_view)
        )

        val adapter = AnimeAdapter { anime, _ -> viewModel.unsubscribe(anime) }
        binding.recyclerViewSubscribed.adapter = adapter
        binding.recyclerViewSubscribed.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.subscribedAnime.collect { list ->
                    adapter.submitList(list)
                    adapter.setSubscribedIds(list.map { it.id }.toSet())
                    binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}