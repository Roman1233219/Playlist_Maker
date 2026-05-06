package com.example.playlistmaker.medialibrary.data

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.TrackDbConvertor
import com.example.playlistmaker.medialibrary.domain.api.PlaylistsRepository
import com.example.playlistmaker.medialibrary.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val convertor: PlaylistDbConvertor,
    private val trackConvertor: TrackDbConvertor
) : PlaylistsRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(convertor.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(convertor.map(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { entities ->
            entities.map { convertor.map(it) }
        }
    }

    override suspend fun getPlaylistById(id: Int): Playlist {
        return convertor.map(appDatabase.playlistDao().getPlaylistById(id))
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        appDatabase.playlistTrackDao().insertTrack(trackConvertor.mapToPlaylistTrack(track))
        updatePlaylist(playlist)
    }
}
