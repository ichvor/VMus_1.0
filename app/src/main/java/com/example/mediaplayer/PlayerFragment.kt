package com.example.mediaplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.databinding.FragmentPlayerBinding

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupAddPlaylistButton()

        // Подписка на обновления списка плейлистов
        sharedViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            (binding.recyclerView.adapter as PlaylistAdapter).submitList(playlists)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PlaylistAdapter { playlist ->
                sharedViewModel.selectPlaylist(playlist)
                Toast.makeText(requireContext(), "Selected: ${playlist.name}", Toast.LENGTH_SHORT).show()
            }
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupAddPlaylistButton() {
        binding.addPlaylistButton.setOnClickListener {
            val playlistName = binding.newPlaylistName.text.toString()
            if (playlistName.isNotBlank()) {
                sharedViewModel.addPlaylist(playlistName)
                binding.newPlaylistName.text.clear()
            } else {
                Toast.makeText(requireContext(), "Enter a playlist name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
