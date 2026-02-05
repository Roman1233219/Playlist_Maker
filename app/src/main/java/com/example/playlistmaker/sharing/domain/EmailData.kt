package com.example.playlistmaker.sharing.domain

data class EmailData(val addresses: Array<String>, val subject: String, val text: String)