package com.example.mediaplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.databinding.ItemPlaylistBinding

class PlaylistAdapter(
    private val onPlaylistClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(PlaylistDiffCallback()) {

    inner class PlaylistViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.root.setOnClickListener {
                onPlaylistClick(playlist)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}
