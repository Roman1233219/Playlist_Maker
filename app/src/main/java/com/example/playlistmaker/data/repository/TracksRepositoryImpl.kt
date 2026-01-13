package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                if (response is TrackSearchResponse) {
                    Resource.Success(response.results.map {
                        Track(
                            it.trackId,
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    })
                } else {
                    Resource.Error("Ошибка сервера")
                }
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}
