package com.example.playlistmaker.domain.api

interface AudioPlayerInteractor {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}
