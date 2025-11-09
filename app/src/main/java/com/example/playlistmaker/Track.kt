package com.example.playlistmaker

import java.io.Serializable
import kotlin.jvm.javaClass
import kotlin.takeIf
import kotlin.text.format
import kotlin.text.isNotEmpty
import kotlin.text.replaceAfterLast
import kotlin.text.substring

data class Track(
   val trackName: String, // Название композиции
   val artistName: String, // Имя исполнителя
   val trackTime: String, // Продолжительность трека
   val  artworkUrl100: String, // Ссылка на изображение обложки
)
fun createTrackList(): ArrayList<Track> {
   val trackList = ArrayList<Track>()

   // Трек 1
   trackList.add(
      Track(
         trackName = "Smells Like Teen Spirit",
         artistName = "Nirvana",
         trackTime = "5:01",
         artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
      )
   )

   // Трек 2
   trackList.add(
      Track(
         trackName = "Billie Jean",
         artistName = "Michael Jackson",
         trackTime = "4:35",
         artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
      )
   )

   // Трек 3
   trackList.add(
      Track(
         trackName = "Stayin' Alive",
         artistName = "Bee Gees",
         trackTime = "4:10",
         artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
      )
   )

   // Трек 4
   trackList.add(
      Track(
         trackName = "Whole Lotta Love",
         artistName = "Led Zeppelin",
         trackTime = "5:33",
         artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
      )
   )

   // Трек 5
   trackList.add(
      Track(
         trackName = "Sweet Child O'Mine",
         artistName = "Guns N' Roses",
         trackTime = "5:03",
         artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a0ddc4bc-2aa8-11e1-8bd7-5e3c6277a066/source/100x100bb.jpg"
      )
   )

   return trackList
}
