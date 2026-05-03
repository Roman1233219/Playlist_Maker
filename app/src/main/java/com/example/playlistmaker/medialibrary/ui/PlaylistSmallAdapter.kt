package com.example.playlistmaker.medialibrary.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistSmallBinding
import com.example.playlistmaker.medialibrary.domain.models.Playlist
import java.io.File

class PlaylistSmallAdapter(private val clickListener: (Playlist) -> Unit) : RecyclerView.Adapter<PlaylistSmallAdapter.PlaylistViewHolder>() {

    var playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener { clickListener(playlist) }
    }

    override fun getItemCount(): Int = playlists.size

    class PlaylistViewHolder(private val binding: ItemPlaylistSmallBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.tracksCount.text = itemView.context.resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.tracksCount,
                playlist.tracksCount
            )

            if (playlist.imagePath != null) {
                Glide.with(itemView)
                    .load(File(playlist.imagePath))
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_2)))
                    .into(binding.playlistCover)
            } else {
                binding.playlistCover.setImageResource(R.drawable.placeholder)
            }
        }
    }
}
