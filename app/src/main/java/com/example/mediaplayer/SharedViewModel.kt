package com.example.mediaplayer

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private var musicService: MusicService? = null
    private var isServiceBound = false

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> get() = _playlists

    private val _currentTrack = MutableLiveData<Track?>()
    val currentTrack: LiveData<Track?> get() = _currentTrack

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _allTracks = MutableLiveData<List<Track>>(emptyList()) // Полный список треков
    private val _filteredTracks = MutableLiveData<List<Track>>(emptyList()) // Отфильтрованный список
    val filteredTracks: LiveData<List<Track>> get() = _filteredTracks

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? MusicService.MusicBinder
            musicService = binder?.getService()
            isServiceBound = true
            musicService?.apply {
                currentTrack.observeForever { _currentTrack.postValue(it) }
                isPlaying.observeForever { _isPlaying.postValue(it) }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isServiceBound = false
        }
    }

    init {
        bindToMusicService()
    }

    private fun bindToMusicService() {
        val appContext = getApplication<Application>().applicationContext
        val intent = Intent(appContext, MusicService::class.java)
        appContext.startService(intent)
        appContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun setTrackList(tracks: List<Track>) {
        if (tracks.isNotEmpty()) {
            _allTracks.postValue(tracks) // Сохраняем полный список
            _filteredTracks.postValue(tracks) // Изначально показываем все треки
            musicService?.setTrackQueue(tracks)
        } else {
            _allTracks.postValue(emptyList()) // Очищаем, если список пуст
            _filteredTracks.postValue(emptyList())
        }
    }

    fun selectTrack(track: Track) {
        musicService?.let {
            it.playTrack(track)
            _currentTrack.postValue(track)
        }
    }

    fun togglePlayPause() {
        musicService?.togglePlayPause()
    }

    fun playNextTrack() {
        musicService?.playNextTrack()
    }

    fun playPreviousTrack() {
        musicService?.playPreviousTrack()
    }

    fun seekTo(position: Int) {
        musicService?.seekTo(position)
    }

    fun getCurrentPosition(): Int {
        return musicService?.getCurrentPosition() ?: 0
    }

    fun getDuration(): Int {
        return musicService?.getDuration() ?: 0
    }

    fun addPlaylist(name: String) {
        val updatedPlaylists = _playlists.value.orEmpty().toMutableList().apply {
            add(Playlist(name = name))
        }
        _playlists.postValue(updatedPlaylists)
    }

    fun selectPlaylist(playlist: Playlist) {
        // Здесь можно реализовать логику фильтрации треков по плейлисту
    }

    fun searchTracks(query: String) {
        val searchQuery = query.trim()
        if (searchQuery.isBlank()) {
            // Если запрос пустой, возвращаем весь список
            _filteredTracks.postValue(_allTracks.value)
            return
        }

        // Фильтруем по названию или исполнителю
        val originalList = _allTracks.value.orEmpty()
        _filteredTracks.postValue(
            originalList.filter { track ->
                track.title.contains(searchQuery, ignoreCase = true) ||
                        track.artist.contains(searchQuery, ignoreCase = true)
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        if (isServiceBound) {
            getApplication<Application>().applicationContext.unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    fun bindService(musicService: MusicService?) {

    }
}
