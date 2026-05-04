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
    private var isClickAllowed = true

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    // Устранение ошибки: сохранение текста и предотвращение повторных запросов
    private var lastSearchText: String? = null
    private var lastSearchQuery: String? = null

    fun searchDebounce(searchText: String) {
        lastSearchQuery = searchText
        if (lastSearchText == searchText) return
        this.lastSearchText = searchText

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
                tracksInteractor
                    .search(searchText)
                    .collect { pair ->
                        val tracks = pair.first
                        val errorMessage = pair.second
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
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    fun showHistory() {
        viewModelScope.launch {
            val history = searchHistoryInteractor.getHistory()
            _state.postValue(SearchState.History(history))
        }
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        _state.postValue(SearchState.History(emptyList()))
    }

    fun getLastQuery(): String? = lastSearchQuery

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
