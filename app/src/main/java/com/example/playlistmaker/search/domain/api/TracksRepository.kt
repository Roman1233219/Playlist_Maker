package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.core.Resource
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}
