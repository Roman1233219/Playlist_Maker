package com.example.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks_table")
data class TrackEntity(
    @PrimaryKey
    val trackId: Long,
    val artworkUrl100: String,
    val trackName: String,
    val artistName: String,
    val collectionName: String?,
    val releaseYear: String?,
    val primaryGenreName: String?,
    val country: String?,
    val trackTime: String?,
    val previewUrl: String?
)
