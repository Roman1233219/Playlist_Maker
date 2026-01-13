package com.example.playlistmaker.data.dto

class TrackSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()
