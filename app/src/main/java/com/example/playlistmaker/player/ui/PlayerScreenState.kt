package com.example.playlistmaker.player.ui

import com.example.playlistmaker.search.domain.models.Track

data class PlayerScreenState(
    val track: Track,
    val playerStatus: PlayerStatus,
    val playProgress: String
)
