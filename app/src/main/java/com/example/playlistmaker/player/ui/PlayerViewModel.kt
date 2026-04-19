package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val interactor: PlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
        private const val DEFAULT_TIME = "00:00"
    }

    private val _screenState = MutableLiveData<PlayerScreenState>(
        PlayerScreenState(track, PlayerStatus.DEFAULT, DEFAULT_TIME)
    )
    val screenState: LiveData<PlayerScreenState> get() = _screenState

    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    private var timerJob: Job? = null

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        interactor.preparePlayer(track.previewUrl ?: "",
            onPrepared = {
                _screenState.value = _screenState.value?.copy(playerStatus = PlayerStatus.PREPARED)
            },
            onCompletion = {
                stopTimer()
                _screenState.value = _screenState.value?.copy(
                    playerStatus = PlayerStatus.PREPARED,
                    playProgress = DEFAULT_TIME
                )
            }
        )
    }

    fun playbackControl() {
        val currentState = _screenState.value ?: return
        when (currentState.playerStatus) {
            PlayerStatus.PLAYING -> pausePlayer()
            PlayerStatus.PREPARED, PlayerStatus.PAUSED -> startPlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        interactor.startPlayer()
        _screenState.value = _screenState.value?.copy(playerStatus = PlayerStatus.PLAYING)
        startTimer()
    }

    fun pausePlayer() {
        interactor.pausePlayer()
        stopTimer()
        val currentPosition = interactor.getCurrentPosition()
        val currentProgress = timeFormat.format(currentPosition)
        _screenState.value = _screenState.value?.copy(
            playerStatus = PlayerStatus.PAUSED,
            playProgress = currentProgress
        )
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (screenState.value?.playerStatus == PlayerStatus.PLAYING) {
                val currentPosition = interactor.getCurrentPosition()
                val currentProgress = timeFormat.format(currentPosition)
                _screenState.value = _screenState.value?.copy(playProgress = currentProgress)
                delay(TIMER_UPDATE_DELAY)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (track.isFavorite) {
                favoriteTracksInteractor.deleteTrackFromFavorites(track)
            } else {
                favoriteTracksInteractor.addTrackToFavorites(track)
            }
            track.isFavorite = !track.isFavorite
            _screenState.value = _screenState.value?.copy(track = track)
        }
    }

    fun releasePlayer() {
        interactor.releasePlayer()
        stopTimer()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}
