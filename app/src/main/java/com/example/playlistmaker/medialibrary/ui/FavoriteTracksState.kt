package com.example.playlistmaker.medialibrary.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoriteTracksState {
    data object Empty : FavoriteTracksState
    data class Content(val tracks: List<Track>) : FavoriteTracksState
}
