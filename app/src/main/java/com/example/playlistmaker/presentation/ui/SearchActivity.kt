package com.example.playlistmaker.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.adapters.TrackAdapter
import com.example.playlistmaker.util.Creator
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var errorLayout: LinearLayout
    private lateinit var noResultsLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button

    private lateinit var historyLayout: View
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button

    private val tracks = mutableListOf<Track>()
    private val historyTracks = mutableListOf<Track>()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var historyInteractor: SearchHistoryInteractor

    private val invalidCharsRegex = Regex("[^a-zA-Zа-яА-Я0-9\\s'.,&-]")

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }

    companion object {
        const val TRACK_KEY = "track"
        private const val SEARCH_TEXT = "TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchContainer = findViewById<View>(R.id.search_container)
        ViewCompat.setOnApplyWindowInsetsListener(searchContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tracksInteractor = Creator.provideTracksInteractor(this)
        historyInteractor = Creator.provideSearchHistoryInteractor(this)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.recycler_view_tracks)
        errorLayout = findViewById(R.id.errorLayout)
        noResultsLayout = findViewById(R.id.noResultsLayout)
        progressBar = findViewById(R.id.progressBar)
        errorImage = findViewById(R.id.errorImage)
        errorText = findViewById(R.id.errorText)
        retryButton = findViewById(R.id.retryButton)

        historyLayout = findViewById(R.id.historyLayout)
        historyRecyclerView = findViewById(R.id.history_recycler)
        clearHistoryButton = findViewById(R.id.clear_history_button)

        val onTrackClick: (Track) -> Unit = { track ->
            historyInteractor.addTrack(track)
            val mediaIntent = Intent(this, MediaActivity::class.java).apply {
                putExtra(TRACK_KEY, track)
            }
            startActivity(mediaIntent)
        }

        trackAdapter = TrackAdapter(tracks, onTrackClick)
        historyAdapter = TrackAdapter(historyTracks, onTrackClick)

        recyclerView.adapter = trackAdapter
        historyRecyclerView.adapter = historyAdapter

        recyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        if (savedInstanceState != null) {
            searchEditText.setText(savedInstanceState.getString(SEARCH_TEXT, ""))
        }

        setupListeners()

        if (searchEditText.text.isEmpty()) {
            showHistory()
        }
    }

    private fun setupListeners() {
        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            showHistory()
        }

        retryButton.setOnClickListener {
            hideKeyboard()
            performSearch()
        }

        clearHistoryButton.setOnClickListener {
            historyInteractor.clearHistory()
            showHistory()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (searchEditText.hasFocus() && s?.isEmpty() == true) {
                    handler.removeCallbacks(searchRunnable)
                    showHistory()
                } else {
                    historyLayout.isVisible = false
                    recyclerView.isVisible = true
                    searchDebounce()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                showHistory()
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                performSearch()
                true
            } else {
                false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchEditText.text.toString())
    }

    private fun showHistory() {
        recyclerView.isVisible = false
        errorLayout.isVisible = false
        noResultsLayout.isVisible = false
        progressBar.isVisible = false

        val savedHistory = historyInteractor.getHistory()
        if (savedHistory.isNotEmpty()) {
            historyTracks.clear()
            historyTracks.addAll(savedHistory)
            historyAdapter.notifyDataSetChanged()
            historyLayout.isVisible = true
        } else {
            historyLayout.isVisible = false
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun performSearch() {
        val rawQuery = searchEditText.text.toString()
        if (rawQuery.isEmpty()) return

        val query = normalizeString(rawQuery)
        if (query.isEmpty()) return

        showPlaceholder(PlaceholderType.LOADING)

        tracksInteractor.search(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                handler.post {
                    if (foundTracks != null) {
                        if (foundTracks.isNotEmpty()) {
                            tracks.clear()
                            tracks.addAll(foundTracks)
                            trackAdapter.notifyDataSetChanged()
                            showPlaceholder(PlaceholderType.RESULTS)
                        } else {
                            showPlaceholder(PlaceholderType.NO_RESULTS)
                        }
                    } else if (errorMessage != null) {
                        showPlaceholder(PlaceholderType.ERROR)
                    }
                }
            }
        })
    }

    private fun normalizeString(input: String?): String {
        if (input == null) return ""
        val cleaned = invalidCharsRegex.replace(input, "")
        return cleaned.replace(Regex("\\s+"), " ").trim()
    }

    private enum class PlaceholderType {
        RESULTS,
        LOADING,
        ERROR,
        NO_RESULTS,
    }

    private fun showPlaceholder(type: PlaceholderType) {
        recyclerView.isVisible = type == PlaceholderType.RESULTS
        progressBar.isVisible = type == PlaceholderType.LOADING
        errorLayout.isVisible = type == PlaceholderType.ERROR
        noResultsLayout.isVisible = type == PlaceholderType.NO_RESULTS

        historyLayout.isVisible = false

        if (type == PlaceholderType.ERROR) {
            errorText.text = getString(R.string.connection_error)
            errorImage.setImageResource(R.drawable.placeholder_internet_error)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}
