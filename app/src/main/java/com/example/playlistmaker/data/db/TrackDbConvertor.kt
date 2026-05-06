package com.example.playlistmaker.data.db

import com.example.playlistmaker.search.domain.models.Track

class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseYear = track.getReleaseYear(),
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTime = track.getFormattedTime(),
            previewUrl = track.previewUrl,
            timestamp = System.currentTimeMillis()
        )
    }

    fun mapToPlaylistTrack(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseYear = track.getReleaseYear(),
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTime = track.getFormattedTime(),
            previewUrl = track.previewUrl,
            timestamp = System.currentTimeMillis()
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackId = trackEntity.trackId,
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            trackTimeMillis = parseFormattedTime(trackEntity.trackTime),
            artworkUrl100 = trackEntity.artworkUrl100,
            collectionName = trackEntity.collectionName,
            releaseDate = trackEntity.releaseYear,
            primaryGenreName = trackEntity.primaryGenreName,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl,
            isFavorite = true
        )
    }

    private fun parseFormattedTime(formattedTime: String?): Long {
        if (formattedTime == null) return 0L
        val parts = formattedTime.split(":")
        if (parts.size != 2) return 0L
        val minutes = parts[0].toLongOrNull() ?: 0L
        val seconds = parts[1].toLongOrNull() ?: 0L
        return (minutes * 60 + seconds) * 1000L
    }
}
