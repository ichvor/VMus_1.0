package com.example.mediaplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MediaPlayerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val serviceIntent = Intent(context, MusicService::class.java).apply {
            this.action = action
        }
        context.startService(serviceIntent)
    }
}
