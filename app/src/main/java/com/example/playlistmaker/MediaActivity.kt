package com.example.playlistmaker

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class MediaActivity : AppCompatActivity() {

    private lateinit var albumArt: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var timerText: TextView
    private lateinit var durationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView
    private lateinit var albumGroup: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val backButton = findViewById<TextView>(R.id.button_back)
        backButton.setOnClickListener { finish() }

        albumArt = findViewById(R.id.album_cover)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        timerText = findViewById(R.id.timer_text)
        durationValue = findViewById(R.id.duration_value)
        albumValue = findViewById(R.id.album_value)
        yearValue = findViewById(R.id.year_value)
        genreValue = findViewById(R.id.genre_value)
        countryValue = findViewById(R.id.country_value)
        albumGroup = findViewById(R.id.album_container)

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("track") as? Track
        }

        if (track != null) {
            Glide.with(this)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(8f)))
                .into(albumArt)

            trackName.text = track.trackName
            artistName.text = track.artistName
            durationValue.text = track.getFormattedTime()
            albumValue.text = track.collectionName ?: ""
            yearValue.text = track.getReleaseYear() ?: ""
            genreValue.text = track.primaryGenreName ?: ""
            countryValue.text = track.country ?: ""

            albumGroup.visibility = if (track.collectionName.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }
}
