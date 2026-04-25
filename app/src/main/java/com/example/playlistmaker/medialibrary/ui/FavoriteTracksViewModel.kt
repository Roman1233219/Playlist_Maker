package com.example.playlistmaker.medialibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.api.FavoriteTracksInteractor
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoriteTracksState>()
    val state: LiveData<FavoriteTracksState> = _state

    init {
        fillData()
    }

    private fun fillData() {
        viewModelScope.launch {
            favoriteTracksInteractor
                .getFavoriteTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<com.example.playlistmaker.search.domain.models.Track>) {
        if (tracks.isEmpty()) {
            _state.postValue(FavoriteTracksState.Empty)
        } else {
            _state.postValue(FavoriteTracksState.Content(tracks))
        }
    }
}
