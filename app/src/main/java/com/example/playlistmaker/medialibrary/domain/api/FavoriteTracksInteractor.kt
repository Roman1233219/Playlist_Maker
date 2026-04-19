package com.example.playlistmaker.medialibrary.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun deleteTrackFromFavorites(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
}
