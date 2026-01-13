package com.example.playlistmaker.domain.models

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Serializable {

    fun getFormattedTime(): String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)


    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    fun getReleaseYear(): String? {
        return releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4)
    }
} // исправлено согласно рекомендации
