package dev.delath.housha.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import dev.delath.housha.R

object NotificationHelper {

    const val CHANNEL_ID = "episode_alerts"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Episode Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifies when a new episode airs for a subscribed anime"
        }
        notificationManager(context).createNotificationChannel(channel)
    }

    fun postNotification(context: Context, animeName: String, episodeNumber: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_menu_subscribed)
            .setContentTitle("New episode available!")
            .setContentText("$animeName – Episode $episodeNumber is now airing")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager(context).notify(animeName.hashCode(), notification)
    }

    private fun notificationManager(context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}
