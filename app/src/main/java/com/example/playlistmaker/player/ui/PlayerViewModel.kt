package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val interactor: PlayerInteractor
) : ViewModel() {

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
        private const val DEFAULT_TIME = "00:00"
    }

    private val _screenState = MutableLiveData<PlayerScreenState>(
        PlayerScreenState(track, PlayerStatus.DEFAULT, DEFAULT_TIME)
    )
    val screenState: LiveData<PlayerScreenState> get() = _screenState

    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    init {
        preparePlayer()
    }

    private fun setState(playerStatus: PlayerStatus, progress: String) {
        val newState = PlayerScreenState(track, playerStatus, progress)
        _screenState.postValue(newState)
    }

    private val updateTimerTask = object : Runnable {
        override fun run() {
            val currentPosition = interactor.getCurrentPosition()
            val currentProgress = timeFormat.format(currentPosition)
            val currentState = _screenState.value
            if (currentState != null) {
                _screenState.postValue(currentState.copy(playerStatus = PlayerStatus.PLAYING, playProgress = currentProgress))
            }
            handler.postDelayed(this, TIMER_UPDATE_DELAY)
        }
    }

    private fun preparePlayer() {
        interactor.preparePlayer(track.previewUrl ?: "",
            onPrepared = {
                val currentState = _screenState.value
                if (currentState != null) {
                    _screenState.postValue(currentState.copy(playerStatus = PlayerStatus.PREPARED))
                }
            },
            onCompletion = {
                handler.removeCallbacks(updateTimerTask)
                val currentState = _screenState.value
                if (currentState != null) {
                    _screenState.postValue(currentState.copy(playerStatus = PlayerStatus.PREPARED, playProgress = DEFAULT_TIME))
                }
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
        handler.post(updateTimerTask)
    }

    fun pausePlayer() {
        interactor.pausePlayer()
        handler.removeCallbacks(updateTimerTask)
        val currentPosition = interactor.getCurrentPosition()
        val currentProgress = timeFormat.format(currentPosition)
        val currentState = _screenState.value
        if (currentState != null) {
             _screenState.postValue(currentState.copy(playerStatus = PlayerStatus.PAUSED, playProgress = currentProgress))
        }
    }

    fun releasePlayer() {
        interactor.releasePlayer()
        handler.removeCallbacks(updateTimerTask)
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}
