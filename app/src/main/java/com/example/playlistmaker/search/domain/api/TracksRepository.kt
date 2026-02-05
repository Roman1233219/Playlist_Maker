package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.core.Resource
import com.example.playlistmaker.search.domain.models.Track

interface TracksRepository {
    suspend fun searchTracks(expression: String): Resource<List<Track>>
}
