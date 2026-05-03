package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.medialibrary.ui.PlaylistSmallAdapter
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val adapter = PlaylistSmallAdapter { playlist ->
        viewModel.addTrackToPlaylist(playlist)
    }

    private val viewModel: PlayerViewModel by viewModel {
        val track = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(TRACK_KEY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable(TRACK_KEY) as? Track
        }
        parametersOf(track)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.playlistRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistRecycler.adapter = adapter

        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPlayPause.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.btnAdd.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.observePlaylists()
        }

        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapter.playlists.clear()
            adapter.playlists.addAll(playlists)
            adapter.notifyDataSetChanged()
        }

        viewModel.addingResult.observe(viewLifecycleOwner) { result ->
            val (playlistName, isAdded) = result
            val message = if (isAdded) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                getString(R.string.track_added_to_playlist, playlistName)
            } else {
                getString(R.string.track_already_in_playlist, playlistName)
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun render(state: PlayerScreenState) {
        setupUI(state.track)
        updatePlayerStatus(state.playerStatus, state.playProgress)
    }

    private fun updatePlayerStatus(status: PlayerStatus, progress: String) {
        binding.timerText.text = progress
        binding.btnPlayPause.isEnabled = status != PlayerStatus.DEFAULT

        when (status) {
            PlayerStatus.PLAYING -> binding.btnPlayPause.setImageResource(R.drawable.pause)
            PlayerStatus.PAUSED, PlayerStatus.PREPARED, PlayerStatus.DEFAULT -> 
                binding.btnPlayPause.setImageResource(R.drawable.play)
        }
    }

    private fun setupUI(track: Track) {
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f)))
            .into(binding.albumCover)

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.durationValue.text = track.getFormattedTime()
        binding.albumValue.text = track.collectionName ?: ""
        binding.yearValue.text = track.getReleaseYear() ?: ""
        binding.genreValue.text = track.primaryGenreName ?: ""
        binding.countryValue.text = track.country ?: ""

        binding.albumContainer.visibility = if (track.collectionName.isNullOrEmpty()) View.GONE else View.VISIBLE

        binding.btnFavorite.setImageResource(
            if (track.isFavorite) R.drawable.ic_favorite_active else R.drawable.izbran
        )
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    companion object {
        const val TRACK_KEY = "track"
    }
}
