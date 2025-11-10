package com.example.playlistmaker

import java.io.Serializable
import kotlin.jvm.javaClass
import kotlin.takeIf
import kotlin.text.format
import kotlin.text.isNotEmpty
import kotlin.text.replaceAfterLast
import kotlin.text.substring

import com.google.gson.annotations.SerializedName

data class Track(
   val trackName: String,
   val artistName: String,

   @SerializedName("trackTimeMillis")
   val trackTimeMillis: Long,

   val artworkUrl100: String
) {

   val trackTime: String
      get() {
         // Рассчитываем минуты и секунды
         val minutes = (trackTimeMillis / 1000) / 60
         val seconds = (trackTimeMillis / 1000) % 60
         // Форматируем в строку "мм:сс"
         return String.format("%02d:%02d", minutes, seconds)
      }
}