package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    suspend fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}
