package com.example.playlistmaker.medialibrary.domain.api

import com.example.playlistmaker.medialibrary.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Int): Playlist
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)
}
