package com.example.mediaplayer

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class MediaPlayerWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_media_player)

        // Подключение действий к кнопкам
        views.setOnClickPendingIntent(
            R.id.playPauseButton,
            getBroadcastIntent(context, MusicService.ACTION_PLAY_PAUSE)
        )
        views.setOnClickPendingIntent(
            R.id.prevButton,
            getBroadcastIntent(context, MusicService.ACTION_PREV)
        )
        views.setOnClickPendingIntent(
            R.id.nextButton,
            getBroadcastIntent(context, MusicService.ACTION_NEXT)
        )

        // Обновление виджета
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getBroadcastIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, MediaPlayerReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
