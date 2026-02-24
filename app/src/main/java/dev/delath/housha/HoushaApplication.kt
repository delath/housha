package dev.delath.housha

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import dev.delath.housha.notification.NotificationHelper
import dev.delath.housha.work.EpisodeCheckWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class HoushaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "episode_check",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<EpisodeCheckWorker>(12, TimeUnit.HOURS).build()
        )
    }
}
