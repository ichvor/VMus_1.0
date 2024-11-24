package com.example.mediaplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.databinding.FragmentTracksBinding
import android.text.Editable
import android.text.TextWatcher

class TracksFragment : Fragment() {

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasPermissions()) {
            requestPermissions()
        } else {
            loadTracks()
        }
    }

    private fun loadTracks() {
        val trackList = mutableListOf<Track>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val cursor = requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getInt(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val path = it.getString(dataColumn)

                trackList.add(Track(id, title, artist, path, ""))
            }
        }

        sharedViewModel.setTrackList(trackList)
        setupRecyclerView(trackList)
    }

    private fun setupRecyclerView(tracks: List<Track>) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = TracksAdapter(tracks) { track ->
                sharedViewModel.selectTrack(track)
            }
        }
    }

    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        ActivityCompat.requestPermissions(requireActivity(), permissions, 100)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
