package com.example.playlistmaker.medialibrary.domain.impl

import com.example.playlistmaker.medialibrary.domain.api.PlaylistsInteractor
import com.example.playlistmaker.medialibrary.domain.api.PlaylistsRepository
import com.example.playlistmaker.medialibrary.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val repository: PlaylistsRepository) : PlaylistsInteractor {
    override suspend fun insertPlaylist(playlist: Playlist) {
        repository.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistById(id: Int): Playlist {
        return repository.getPlaylistById(id)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        repository.addTrackToPlaylist(playlist, track)
    }
}
