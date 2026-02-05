package com.example.playlistmaker.search.data.dto

class TrackSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()
