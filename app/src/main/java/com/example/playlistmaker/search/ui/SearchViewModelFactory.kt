package com.example.playlistmaker.search.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator

class SearchViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            val tracksInteractor = Creator.provideTracksInteractor(application)
            val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(application)
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(tracksInteractor, searchHistoryInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}