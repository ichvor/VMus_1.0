package com.example.mediaplayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mediaplayer.databinding.ActivityMediaPlayerBinding

class MediaPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaPlayerBinding
    private val sharedViewModel: SharedViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
        setupSeekBar()
    }

    private fun setupUI() {
        binding.playPauseButton.setOnClickListener {
            sharedViewModel.togglePlayPause()
        }

        binding.nextButton.setOnClickListener {
            sharedViewModel.playNextTrack()
        }

        binding.prevButton.setOnClickListener {
            sharedViewModel.playPreviousTrack()
        }
    }

    private fun setupObservers() {
        sharedViewModel.currentTrack.observe(this) { track ->
            binding.trackTitle.text = track?.title ?: "Unknown Title"
            binding.trackArtist.text = track?.artist ?: "Unknown Artist"
        }

        sharedViewModel.isPlaying.observe(this) { isPlaying ->
            binding.playPauseButton.setImageResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
    }

    private fun setupSeekBar() {
        binding.trackSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) sharedViewModel.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        handler.post(object : Runnable {
            override fun run() {
                val position = sharedViewModel.getCurrentPosition()
                val duration = sharedViewModel.getDuration()

                binding.trackSeekBar.progress = position
                binding.trackSeekBar.max = duration

                binding.elapsedTime.text = formatTime(position)
                binding.totalTime.text = formatTime(duration)

                handler.postDelayed(this, 500)
            }
        })
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
