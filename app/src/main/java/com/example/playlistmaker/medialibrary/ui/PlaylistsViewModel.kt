package com.example.playlistmaker.medialibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.api.PlaylistsInteractor
import com.example.playlistmaker.medialibrary.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val interactor: PlaylistsInteractor) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    fun fillData() {
        viewModelScope.launch {
            interactor.getPlaylists().collect { list ->
                _playlists.postValue(list)
            }
        }
    }
}
