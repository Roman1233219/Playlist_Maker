package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    suspend fun search(expression: String): Pair<List<Track>?, String?>
}
