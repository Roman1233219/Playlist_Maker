package com.example.playlistmaker.medialibrary.domain.models

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val trackIds: List<Long>,
    val tracksCount: Int
)
