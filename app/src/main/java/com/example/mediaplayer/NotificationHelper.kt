package com.example.mediaplayer

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "media_player_channel"
        const val NOTIFICATION_ID = 1
    }

    fun createNotification(
        track: Track?,
        isPlaying: Boolean,
        playPauseAction: PendingIntent,
        nextAction: PendingIntent,
        prevAction: PendingIntent
    ): Notification {
        val openAppIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(track?.title ?: "No Track")
            .setContentText(track?.artist ?: "Unknown Artist")
            .setSmallIcon(R.drawable.ic_music_note)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_album))
            .setContentIntent(openAppIntent)
            .setStyle(MediaStyle())
            .addAction(
                R.drawable.ic_previous, "Previous",
                prevAction
            )
            .addAction(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                if (isPlaying) "Pause" else "Play",
                playPauseAction
            )
            .addAction(
                R.drawable.ic_next, "Next",
                nextAction
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(isPlaying)
            .build()
    }
}
