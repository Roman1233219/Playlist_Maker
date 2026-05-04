package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.adapters.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private val tracks = mutableListOf<Track>()
    private val historyTracks = mutableListOf<Track>()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onTrackClick: (Track) -> Unit = { track ->
            if (viewModel.clickDebounce()) {
                viewModel.addTrackToHistory(track)
                val bundle = Bundle().apply {
                    putSerializable(PlayerFragment.TRACK_KEY, track)
                }
                findNavController().navigate(R.id.action_searchFragment_to_playerFragment, bundle)
            }
        }

        trackAdapter = TrackAdapter(tracks, onTrackClick)
        historyAdapter = TrackAdapter(historyTracks, onTrackClick)

        binding.recyclerViewTracks.adapter = trackAdapter
        binding.historyRecycler.adapter = historyAdapter

        binding.recyclerViewTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecycler.layoutManager = LinearLayoutManager(requireContext())

        setupListeners()

        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }

        // Устранение ошибки: восстановление текста поиска при возвращении на экран
        val lastQuery = viewModel.getLastQuery()
        if (!lastQuery.isNullOrEmpty() && binding.searchEditText.text.isEmpty()) {
            binding.searchEditText.setText(lastQuery)
            binding.searchEditText.setSelection(lastQuery.length)
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
        binding.recyclerViewTracks.isVisible = false
        binding.historyLayout.isVisible = false
        binding.noResultsLayout.isVisible = false
        binding.errorLayout.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun showContent(newTracks: List<Track>) {
        binding.progressBar.isVisible = false
        binding.noResultsLayout.isVisible = false
        binding.errorLayout.isVisible = false
        binding.historyLayout.isVisible = false
        binding.recyclerViewTracks.isVisible = true

        tracks.clear()
        tracks.addAll(newTracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showError(errorMessage: String) {
        binding.progressBar.isVisible = false
        binding.recyclerViewTracks.isVisible = false
        binding.historyLayout.isVisible = false
        binding.noResultsLayout.isVisible = false
        binding.errorLayout.isVisible = true
        binding.errorText.text = errorMessage
    }

    private fun showEmpty() {
        binding.progressBar.isVisible = false
        binding.recyclerViewTracks.isVisible = false
        binding.historyLayout.isVisible = false
        binding.errorLayout.isVisible = false
        binding.noResultsLayout.isVisible = true
    }

    private fun showHistory(history: List<Track>) {
        binding.progressBar.isVisible = false
        binding.recyclerViewTracks.isVisible = false
        binding.noResultsLayout.isVisible = false
        binding.errorLayout.isVisible = false
        if (history.isNotEmpty()) {
            historyTracks.clear()
            historyTracks.addAll(history)
            historyAdapter.notifyDataSetChanged()
            binding.historyLayout.isVisible = true
        } else {
            binding.historyLayout.isVisible = false
        }
    }

    private fun setupListeners() {
        binding.clearButton.setOnClickListener {
            binding.searchEditText.setText("")
            hideKeyboard()
            viewModel.showHistory()
        }

        binding.retryButton.setOnClickListener {
            hideKeyboard()
            viewModel.searchImmediately(binding.searchEditText.text.toString())
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearButton.isVisible = !s.isNullOrEmpty()
                if (binding.searchEditText.hasFocus() && s?.isEmpty() == true) {
                    viewModel.showHistory()
                } else {
                    viewModel.searchDebounce(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) {
                viewModel.showHistory()
            }
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                viewModel.searchImmediately(binding.searchEditText.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}