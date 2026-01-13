package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    fun searchTracks(
        @Query("term") searchTerm: String,
        @Query("limit") limit: Int = 50 //не знаю надо лимит или нет но поставлю
    ): Call<TrackSearchResponse>
}
