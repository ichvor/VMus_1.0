package com.example.mediaplayer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mediaplayer.databinding.ActivityMainBinding
import com.example.mediaplayer.databinding.LayoutMiniPlayerBinding
import com.google.android.material.tabs.TabLayoutMediator
import android.text.Editable
import android.text.TextWatcher


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var miniPlayerBinding: LayoutMiniPlayerBinding
    private lateinit var viewModel: SharedViewModel
    private var musicService: MusicService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? MusicService.MusicBinder
            musicService = binder?.getService()
            isServiceBound = true
            viewModel.bindService(musicService)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        miniPlayerBinding = LayoutMiniPlayerBinding.bind(binding.miniPlayer.root)
        binding.miniPlayer.root.visibility = android.view.View.GONE // Мини-плеер скрыт по умолчанию

        checkAndRequestNotificationPermission()

        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Tracks"
                1 -> "Playlists"
                else -> "Tab $position"
            }
        }.attach()

        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        observeViewModel()
        bindToMusicService()

        setContentView(binding.root)

    }


    private fun passSearchQueryToFragments(query: String) {
        val currentFragment = supportFragmentManager.findFragmentByTag("f${binding.viewPager.currentItem}")
        if (currentFragment is Searchable) {
            currentFragment.onSearchQueryChanged(query)
        }
    }

    interface Searchable {
        fun onSearchQueryChanged(query: String)
    }

    private fun bindToMusicService() {
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.currentTrack.observe(this) { track ->
            if (track != null) {
                miniPlayerBinding.miniPlayerTrackTitle.text = track.title
                miniPlayerBinding.miniPlayerArtist.text = track.artist
                binding.miniPlayer.root.visibility = android.view.View.VISIBLE
            } else {
                binding.miniPlayer.root.visibility = android.view.View.GONE
            }
        }

        viewModel.isPlaying.observe(this) { isPlaying ->
            miniPlayerBinding.miniPlayerPlayPauseButton.setImageResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        miniPlayerBinding.miniPlayerNextButton.setOnClickListener { viewModel.playNextTrack() }
        miniPlayerBinding.miniPlayerPreviousButton.setOnClickListener { viewModel.playPreviousTrack() }
        miniPlayerBinding.miniPlayerPlayPauseButton.setOnClickListener { viewModel.togglePlayPause() }

        miniPlayerBinding.root.setOnClickListener {
            viewModel.currentTrack.value?.let { track -> openMediaPlayer(track) }
        }
    }

    private fun openMediaPlayer(track: Track) {
        val intent = Intent(this, MediaPlayerActivity::class.java).apply {
            putExtra("TRACK", track)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
}
