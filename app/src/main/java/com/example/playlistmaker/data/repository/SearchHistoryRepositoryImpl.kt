package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<TrackDto>>() {}.type
            val dtoHistory: List<TrackDto> = gson.fromJson(json, type)
            dtoHistory.map { mapToDomain(it) }
        } else {
            emptyList()
        }
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        
        val limitedHistory = history.take(MAX_HISTORY_SIZE)
        val dtoHistory = limitedHistory.map { mapToDto(it) }
        
        val json = gson.toJson(dtoHistory)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }

    private fun mapToDomain(dto: TrackDto): Track {
        return Track(
            dto.trackId, dto.trackName, dto.artistName, dto.trackTimeMillis,
            dto.artworkUrl100, dto.collectionName, dto.releaseDate,
            dto.primaryGenreName, dto.country, dto.previewUrl
        )
    }

    private fun mapToDto(track: Track): TrackDto {
        return TrackDto(
            track.trackId, track.trackName, track.artistName, track.trackTimeMillis,
            track.artworkUrl100, track.collectionName, track.releaseDate,
            track.primaryGenreName, track.country, track.previewUrl
        )
    }

    private companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }
}
