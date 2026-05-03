package com.example.playlistmaker.medialibrary.data

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.medialibrary.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imagePath = playlist.imagePath,
            trackIds = gson.toJson(playlist.trackIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Long>>() {}.type
        return Playlist(
            id = playlistEntity.id,
            name = playlistEntity.name,
            description = playlistEntity.description,
            imagePath = playlistEntity.imagePath,
            trackIds = gson.fromJson(playlistEntity.trackIds, type) ?: emptyList(),
            tracksCount = playlistEntity.tracksCount
        )
    }
}
