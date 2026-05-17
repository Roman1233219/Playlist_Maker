package com.example.playlistmaker.medialibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.api.PlaylistsInteractor
import com.example.playlistmaker.medialibrary.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.SharingInteractor
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistId: Int,
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlistLiveData

    private val tracksLiveData = MutableLiveData<List<Track>>()
    fun observeTracks(): LiveData<List<Track>> = tracksLiveData

    fun loadPlaylist() {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId)
            playlistLiveData.postValue(playlist)
            
            playlistsInteractor.getTracksFromPlaylist(playlist.trackIds).collect { tracks ->
                tracksLiveData.postValue(tracks)
            }
        }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            val playlist = playlistLiveData.value ?: return@launch
            playlistsInteractor.deleteTrackFromPlaylist(playlist, track)
            loadPlaylist()
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            val playlist = playlistLiveData.value ?: return@launch
            playlistsInteractor.deletePlaylist(playlist)
        }
    }

    fun sharePlaylist() {
        val playlist = playlistLiveData.value ?: return
        val tracks = tracksLiveData.value ?: emptyList()
        
        val sb = StringBuilder()
        sb.append(playlist.name).append("\n")
        if (!playlist.description.isNullOrEmpty()) {
            sb.append(playlist.description).append("\n")
        }
        
        // Передаем количество треков

        sb.append(tracks.size).append(" треков\n") 

        tracks.forEachIndexed { index, track ->
            sb.append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.getFormattedTime()})\n")
        }
        
        sharingInteractor.shareText(sb.toString())
    }
}
