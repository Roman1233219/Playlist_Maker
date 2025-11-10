package com.example.playlistmaker

import android.content.Context
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private val tracks = mutableListOf<Track>()
    private val trackAdapter = TrackAdapter(tracks)
    private val iTunesService = RetrofitClient.api

    private val invalidCharsRegex = Regex("[^a-zA-Zа-яА-Я0-9\\s'.,&-]")

    companion object {
        private const val SEARCH_TEXT = "TEXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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

        recyclerView.adapter = trackAdapter

        if (savedInstanceState != null) {
            searchEditText.setText(savedInstanceState.getString(SEARCH_TEXT, ""))
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            showPlaceholder(PlaceholderType.EMPTY)
        }

        retryButton.setOnClickListener {
            performSearch()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) { }
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
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

    private fun isInputValid(input: String): Boolean {
        return !invalidCharsRegex.containsMatchIn(input)
    }

    private fun normalizeString(input: String?): String {
        if (input == null) return ""
        val cleaned = invalidCharsRegex.replace(input, "")
        return cleaned.replace(Regex("\\s+"), " ").trim()
    }

    private fun performSearch() {
        val rawQuery = searchEditText.text.toString()
        if (rawQuery.isEmpty()) return

        if (!isInputValid(rawQuery)) {
            Toast.makeText(applicationContext, "Введены недопустимые символы", Toast.LENGTH_SHORT).show()
            return
        }

        val query = normalizeString(rawQuery)
        if (query.isEmpty()) return

        hideKeyboard()
        showPlaceholder(PlaceholderType.LOADING)

        iTunesService.searchTracks(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    val foundTracks = response.body()?.results
                    if (!foundTracks.isNullOrEmpty()) {
                        val cleanedTracks = foundTracks.map { track ->
                            track.copy(
                                trackName = normalizeString(track.trackName),
                                artistName = normalizeString(track.artistName)
                            )
                        }
                        tracks.clear()
                        tracks.addAll(cleanedTracks)
                        trackAdapter.notifyDataSetChanged()
                        showPlaceholder(PlaceholderType.EMPTY)
                    } else {
                        showPlaceholder(PlaceholderType.NO_RESULTS)
                    }
                } else {
                    showPlaceholder(PlaceholderType.ERROR)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showPlaceholder(PlaceholderType.ERROR)
            }
        })
    }

    private enum class PlaceholderType {
        EMPTY,
        LOADING,
        ERROR,
        NO_RESULTS
    }

    private fun showPlaceholder(type: PlaceholderType) {
        recyclerView.isVisible = type == PlaceholderType.EMPTY
        progressBar.isVisible = type == PlaceholderType.LOADING
        errorLayout.isVisible = type == PlaceholderType.ERROR
        noResultsLayout.isVisible = type == PlaceholderType.NO_RESULTS

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
