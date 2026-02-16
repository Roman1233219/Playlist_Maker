package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.adapters.TrackAdapter
import com.example.playlistmaker.player.ui.MediaActivity
import com.google.android.material.appbar.MaterialToolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

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

    private val viewModel: SearchViewModel by viewModel()

    companion object {
        const val TRACK_KEY = "track"
        private const val SEARCH_TEXT = "SEARCH_TEXT"
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
            viewModel.addTrackToHistory(track)
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

        viewModel.state.observe(this) {
            render(it)
        }

        if (searchEditText.text.isEmpty()) {
            viewModel.showHistory()
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError(state.message)
            is SearchState.Empty -> showEmpty()
            is SearchState.History -> showHistory(state.tracks)
        }
    }

    private fun showLoading() {
        recyclerView.isVisible = false
        historyLayout.isVisible = false
        noResultsLayout.isVisible = false
        errorLayout.isVisible = false
        progressBar.isVisible = true
    }

    private fun showContent(newTracks: List<Track>) {
        progressBar.isVisible = false
        noResultsLayout.isVisible = false
        errorLayout.isVisible = false
        historyLayout.isVisible = false
        recyclerView.isVisible = true

        tracks.clear()
        tracks.addAll(newTracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showError(errorMessage: String) {
        progressBar.isVisible = false
        recyclerView.isVisible = false
        historyLayout.isVisible = false
        noResultsLayout.isVisible = false
        errorLayout.isVisible = true
        errorText.text = errorMessage
    }

    private fun showEmpty() {
        progressBar.isVisible = false
        recyclerView.isVisible = false
        historyLayout.isVisible = false
        errorLayout.isVisible = false
        noResultsLayout.isVisible = true
    }

    private fun showHistory(history: List<Track>) {
        progressBar.isVisible = false
        recyclerView.isVisible = false
        noResultsLayout.isVisible = false
        errorLayout.isVisible = false
        if(history.isNotEmpty()){
            historyTracks.clear()
            historyTracks.addAll(history)
            historyAdapter.notifyDataSetChanged()
            historyLayout.isVisible = true
        } else {
            historyLayout.isVisible = false
        }

    }

    private fun setupListeners() {
        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
            viewModel.showHistory()
        }

        retryButton.setOnClickListener {
            hideKeyboard()
            viewModel.searchImmediately(searchEditText.text.toString())
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (searchEditText.hasFocus() && s?.isEmpty() == true) {
                    viewModel.showHistory()
                } else {
                    viewModel.searchDebounce(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                viewModel.showHistory()
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                viewModel.searchImmediately(searchEditText.text.toString())
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

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}
