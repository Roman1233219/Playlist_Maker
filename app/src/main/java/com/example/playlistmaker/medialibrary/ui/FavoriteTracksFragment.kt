package com.example.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.adapters.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private val viewModel: FavoriteTracksViewModel by viewModel()

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private var trackAdapter: TrackAdapter? = null

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            openPlayer(track)
        }

        binding.favoriteTracksRecycler.adapter = trackAdapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: FavoriteTracksState) {
        when (state) {
            is FavoriteTracksState.Content -> showContent(state.tracks)
            is FavoriteTracksState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.emptyFavoriteLayout.isVisible = true
        binding.favoriteTracksRecycler.isVisible = false
    }

    private fun showContent(tracks: List<Track>) {
        binding.emptyFavoriteLayout.isVisible = false
        binding.favoriteTracksRecycler.isVisible = true

        trackAdapter?.updateTracks(tracks)
    }

    private fun openPlayer(track: Track) {
        val bundle = Bundle().apply {
            putSerializable(PlayerFragment.TRACK_KEY, track)
        }
        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_playerFragment,
            bundle
        )
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        trackAdapter = null
    }
}
