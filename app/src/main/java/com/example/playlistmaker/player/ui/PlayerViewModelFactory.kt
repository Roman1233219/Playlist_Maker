package com.example.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.models.Track

class PlayerViewModelFactory(private val track: Track) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(
                track = track,
                interactor = Creator.providePlayerInteractor()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
