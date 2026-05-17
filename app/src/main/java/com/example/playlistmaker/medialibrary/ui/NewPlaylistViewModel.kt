package com.example.playlistmaker.medialibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.api.PlaylistsInteractor
import com.example.playlistmaker.medialibrary.domain.models.Playlist
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(private val interactor: PlaylistsInteractor) : ViewModel() {

    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlistLiveData

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

    fun getPlaylist(id: Int) {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(id)
            playlistLiveData.postValue(playlist)
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            interactor.updatePlaylist(playlist)
        }
    }
}
