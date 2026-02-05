package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    object Loading : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    data class Error(val message: String) : SearchState
    object Empty : SearchState
    data class History(val tracks: List<Track>) : SearchState
}
