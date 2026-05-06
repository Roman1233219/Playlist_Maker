package com.example.playlistmaker.medialibrary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.api.PlaylistsInteractor
import com.example.playlistmaker.medialibrary.domain.models.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val interactor: PlaylistsInteractor) : ViewModel() {

    fun createPlaylist(name: String, description: String?, imagePath: String?) {
        viewModelScope.launch {
            interactor.insertPlaylist(
                Playlist(
                    name = name,
                    description = description,
                    imagePath = imagePath,
                    trackIds = emptyList(),
                    tracksCount = 0
                )
            )
        }
    }
}
