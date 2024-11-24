package com.example.mediaplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MusicService : Service() {

    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentTrack = MutableLiveData<Track?>()
    val currentTrack: LiveData<Track?> get() = _currentTrack

    private val trackQueue = mutableListOf<Track>()
    private var currentIndex = 0

    private val handler = Handler()
    private val positionUpdateRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    Log.d("MusicService", "Current position: ${it.currentPosition}")
                }
            }
            handler.postDelayed(this, 1000) // Обновляем каждую секунду
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d("MusicService", "Service bound")
        return binder
    }

    fun setTrackQueue(tracks: List<Track>) {
        trackQueue.clear()
        trackQueue.addAll(tracks)
        Log.d("MusicService", "Track queue set: ${tracks.map { it.title }}")
        if (trackQueue.isNotEmpty()) {
            currentIndex = 0
            playTrack(trackQueue[currentIndex]) // Автовоспроизведение первого трека
        } else {
            _currentTrack.postValue(null)
            _isPlaying.postValue(false)
        }
    }

    fun playTrack(track: Track) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(track.path)
                prepare()
                start()
                setOnCompletionListener { playNextTrack() }
                setOnErrorListener { _, _, _ ->
                    Log.e("MusicService", "Error playing track: ${track.title}")
                    _isPlaying.postValue(false)
                    stopSelf()
                    true
                }
            }
            currentIndex = trackQueue.indexOf(track) // Устанавливаем текущий индекс
            _currentTrack.postValue(track)
            _isPlaying.postValue(true)
            showNotification(track, true)
        } catch (e: Exception) {
            Log.e("MusicService", "Exception playing track: ${track.title}", e)
            _isPlaying.postValue(false)
        }
    }

    fun playNextTrack() {
        if (trackQueue.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % trackQueue.size // Переход к следующему треку
            playTrack(trackQueue[currentIndex])
        } else {
            Log.w("MusicService", "No next track available")
            _isPlaying.postValue(false)
            _currentTrack.postValue(null)
        }
    }

    fun playPreviousTrack() {
        if (trackQueue.isNotEmpty()) {
            currentIndex = (currentIndex - 1 + trackQueue.size) % trackQueue.size // Переход к предыдущему треку
            playTrack(trackQueue[currentIndex])
        } else {
            Log.w("MusicService", "No previous track available")
            _isPlaying.postValue(false)
            _currentTrack.postValue(null)
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                Log.d("MusicService", "Track paused: ${_currentTrack.value?.title}")
                _isPlaying.postValue(false)
            } else {
                it.start()
                Log.d("MusicService", "Track resumed: ${_currentTrack.value?.title}")
                _isPlaying.postValue(true)
            }
        } ?: Log.e("MusicService", "MediaPlayer is null")
        _currentTrack.value?.let { track ->
            showNotification(track, _isPlaying.value == true)
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        Log.d("MusicService", "Seek to position: $position")
    }

    fun getCurrentPosition(): Int {
        val position = mediaPlayer?.currentPosition ?: 0
        Log.d("MusicService", "Current position: $position")
        return position
    }

    fun getDuration(): Int {
        val duration = mediaPlayer?.duration ?: 0
        Log.d("MusicService", "Track duration: $duration")
        return duration
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> togglePlayPause()
            ACTION_NEXT -> playNextTrack()
            ACTION_PREV -> playPreviousTrack()
        }
        return START_NOT_STICKY
    }

    private fun showNotification(track: Track, isPlaying: Boolean) {
        val channelId = "music_service_channel"
        val channelName = "Music Playback"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationManager.createNotificationChannel(channel)
            }
        }

        val playPauseIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, MediaPlayerReceiver::class.java).apply { action = ACTION_PLAY_PAUSE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = PendingIntent.getBroadcast(
            this,
            1,
            Intent(this, MediaPlayerReceiver::class.java).apply { action = ACTION_NEXT },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = PendingIntent.getBroadcast(
            this,
            2,
            Intent(this, MediaPlayerReceiver::class.java).apply { action = ACTION_PREV },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(track.title ?: "Unknown Track")
            .setContentText(track.artist ?: "Unknown Artist")
            .setSmallIcon(R.drawable.iconhelp1)
            .addAction(R.drawable.ic_previous, "Previous", prevIntent)
            .addAction(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                if (isPlaying) "Pause" else "Play",
                playPauseIntent
            )
            .addAction(R.drawable.ic_next, "Next", nextIntent)
            .setOngoing(isPlaying)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(1, notification)
    }

    private fun startPositionUpdates() {
        handler.post(positionUpdateRunnable)
    }

    private fun stopPositionUpdates() {
        handler.removeCallbacks(positionUpdateRunnable)
    }

    override fun onDestroy() {
        Log.d("MusicService", "Service destroyed")
        stopPositionUpdates()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.mediaplayer.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.mediaplayer.ACTION_NEXT"
        const val ACTION_PREV = "com.example.mediaplayer.ACTION_PREV"
    }
}