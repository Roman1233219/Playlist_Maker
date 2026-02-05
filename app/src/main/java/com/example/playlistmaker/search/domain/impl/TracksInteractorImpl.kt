package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.core.Resource
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    override suspend fun search(expression: String): Pair<List<Track>?, String?> {
        return when(val result = repository.searchTracks(expression)) {
            is Resource.Success -> {
                Pair(result.data, null)
            }
            is Resource.Error -> {
                Pair(null, result.message)
            }
        }
    }
}
