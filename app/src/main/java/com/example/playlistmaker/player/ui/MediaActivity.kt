package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

// Импорты
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.player.ui.PlayerScreenState
import com.example.playlistmaker.player.ui.PlayerStatus

class MediaActivity : AppCompatActivity() {

    companion object {
        const val TRACK_KEY = "track"
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

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(intent.getSerializableExtra(TRACK_KEY) as? Track)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(TRACK_KEY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(TRACK_KEY) as? Track
        }

        if (track == null) {
            finish()
            return
        }

        val rootLayout = findViewById<View>(R.id.album_cover).rootView
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

        playPauseButton.setOnClickListener {
            viewModel.playbackControl()
        }

        viewModel.screenState.observe(this) { state ->
            render(state)
        }
    }

    private fun render(state: PlayerScreenState) {
        // Просто обновляем UI, так как у нас всегда есть полное состояние
        setupUI(state.track)
        updatePlayerStatus(state.playerStatus, state.playProgress)
    }

    private fun updatePlayerStatus(status: PlayerStatus, progress: String) {
        timerText.text = progress
        playPauseButton.isEnabled = status != PlayerStatus.DEFAULT

        when (status) {
            PlayerStatus.PLAYING -> playPauseButton.setImageResource(R.drawable.pause)
            PlayerStatus.PAUSED, PlayerStatus.PREPARED, PlayerStatus.DEFAULT -> playPauseButton.setImageResource(R.drawable.play)
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

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }
}
