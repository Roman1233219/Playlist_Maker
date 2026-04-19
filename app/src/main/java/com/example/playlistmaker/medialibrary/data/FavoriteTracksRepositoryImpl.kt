package com.example.playlistmaker.medialibrary.data

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.TrackDbConvertor
import com.example.playlistmaker.medialibrary.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteTracksRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConvertor.map(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getTracks().map { tracks ->
            tracks.map { trackDbConvertor.map(it) }
        }
    }
}
