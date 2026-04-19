package com.example.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase
) : SearchHistoryRepository {

    override suspend fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<TrackDto>>() {}.type
            val dtoHistory: List<TrackDto> = gson.fromJson(json, type)
            val favoriteTracksIds = appDatabase.trackDao().getTracksIds()
            dtoHistory.map { dto ->
                mapToDomain(dto).apply {
                    isFavorite = favoriteTracksIds.contains(dto.trackId)
                }
            }
        } else {
            emptyList()
        }
    }

    override fun addTrack(track: Track) {
        val historyJson = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
        val type = object : TypeToken<List<TrackDto>>() {}.type
        val history: MutableList<TrackDto> = if (historyJson != null) {
            gson.fromJson(historyJson, type)
        } else {
            mutableListOf()
        }

        history.removeAll { it.trackId == track.trackId }
        history.add(0, mapToDto(track))

        val limitedHistory = history.take(MAX_HISTORY_SIZE)

        val json = gson.toJson(limitedHistory)
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
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100,
            collectionName = dto.collectionName,
            releaseDate = dto.releaseDate,
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl
        )
    }

    private fun mapToDto(track: Track): TrackDto {
        return TrackDto(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    private companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }
}
