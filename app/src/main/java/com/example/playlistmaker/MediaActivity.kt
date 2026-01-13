package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.playlistmaker.SearchActivity.Companion.TRACK_KEY

class MediaActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIMER_UPDATE_DELAY = 300L
    }

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
    private lateinit var playPauseButton: ImageButton

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    
    private val mainHandler = Handler(Looper.getMainLooper())

    private val updateTimerTask = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                timerText.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(mediaPlayer.currentPosition)
                mainHandler.postDelayed(this, TIMER_UPDATE_DELAY)
            }
        }
    }

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
        playPauseButton = findViewById(R.id.btn_play_pause)

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(TRACK_KEY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(TRACK_KEY) as? Track
        }

        if (track != null) {
            setupUI(track)
            track.previewUrl?.let { preparePlayer(it) }
        }

        playPauseButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun setupUI(track: Track) {
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

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playPauseButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            pausePlayer()
            timerText.text = "00:00"
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playPauseButton.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        mainHandler.post(updateTimerTask)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playPauseButton.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
        mainHandler.removeCallbacks(updateTimerTask)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainHandler.removeCallbacks(updateTimerTask)
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }
}
