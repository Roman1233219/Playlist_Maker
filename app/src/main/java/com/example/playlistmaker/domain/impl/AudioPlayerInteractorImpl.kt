package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.AudioPlayerInteractor

class AudioPlayerInteractorImpl : AudioPlayerInteractor {

    private var mediaPlayer = MediaPlayer()

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { onPrepared() }
        mediaPlayer.setOnCompletionListener { onCompletion() }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }
}
