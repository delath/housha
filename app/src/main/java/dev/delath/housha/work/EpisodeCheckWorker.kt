package dev.delath.housha.work

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.delath.housha.data.AnimeRepository
import dev.delath.housha.notification.NotificationHelper

@HiltWorker
class EpisodeCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: AnimeRepository
) : CoroutineWorker(context, params) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("episode_tracker", Context.MODE_PRIVATE)

    override suspend fun doWork(): Result = try {
        val nowSeconds = System.currentTimeMillis() / 1000
        repository.getAllSubscribedOnce().forEach { anime ->
            val nextEpNum = anime.nextEpisodeNumber ?: return@forEach
            val nextEpAt  = anime.nextEpisodeAt     ?: return@forEach
            val prefKey   = "last_notified_ep_${anime.id}"
            val lastNotified = prefs.getInt(prefKey, -1)
            if (nextEpAt <= nowSeconds && nextEpNum > lastNotified) {
                NotificationHelper.postNotification(applicationContext, anime.title, nextEpNum)
                prefs.edit().putInt(prefKey, nextEpNum).apply()
            }
        }
        Result.success()
    } catch (e: Exception) {
        Result.retry()
    }
}
