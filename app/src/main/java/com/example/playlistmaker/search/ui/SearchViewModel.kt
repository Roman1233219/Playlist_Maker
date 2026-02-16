package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private var searchJob: Job? = null

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    fun searchDebounce(searchText: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchImmediately(searchText)
        }
    }

    fun searchImmediately(searchText: String) {
        if (searchText.isNotEmpty()) {
            _state.postValue(SearchState.Loading)

            viewModelScope.launch {
                val (tracks, errorMessage) = tracksInteractor.search(searchText)
                if (tracks != null) {
                    if (tracks.isNotEmpty()) {
                        _state.postValue(SearchState.Content(tracks))
                    } else {
                        _state.postValue(SearchState.Empty)
                    }
                } else if (errorMessage != null) {
                    _state.postValue(SearchState.Error(errorMessage))
                }
            }
        }
    }

    fun showHistory() {
        val history = searchHistoryInteractor.getHistory()
        _state.postValue(SearchState.History(history))
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        _state.postValue(SearchState.History(emptyList()))
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
