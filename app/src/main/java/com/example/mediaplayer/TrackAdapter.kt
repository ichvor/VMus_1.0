package com.example.mediaplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.databinding.ItemTrackBinding

class TracksAdapter(
    private var tracks: List<Track>,
    private val onItemClick: (Track) -> Unit
) : RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {

    /**
     * Updates the tracks list using DiffUtil for better performance.
     */
    fun updateTracks(newTracks: List<Track>) {
        val diffCallback = TracksDiffCallback(tracks, newTracks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        tracks = newTracks
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    inner class TrackViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.trackTitle.text = track.title
            binding.trackArtist.text = track.artist
            itemView.setOnClickListener { onItemClick(track) }
        }
    }

    /**
     * Custom DiffUtil.Callback implementation for Track items.
     */
    class TracksDiffCallback(
        private val oldList: List<Track>,
        private val newList: List<Track>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Compare by unique ID (or another unique property)
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Compare full contents
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
