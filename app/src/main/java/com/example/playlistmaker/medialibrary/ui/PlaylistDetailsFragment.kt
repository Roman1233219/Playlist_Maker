package com.example.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import android.util.TypedValue
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.medialibrary.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.adapters.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private var playlistId: Int = 0

    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(playlistId)
    }

    private var trackAdapter: TrackAdapter? = null

    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = arguments?.getInt("playlistId") ?: 0

        setupRecyclerView()
        setupBottomSheets()
        setupListeners()

        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            renderPlaylist(playlist)
        }

        viewModel.observeTracks().observe(viewLifecycleOwner) { tracks ->
            renderTracks(tracks)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylist()
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(
            tracks = emptyList(),
            onTrackClick = { track ->
                findNavController().navigate(
                    R.id.action_playlistDetailsFragment_to_playerFragment,
                    bundleOf("track" to track)
                )
            },
            onLongClick = { track ->
                showDeleteTrackDialog(track)
            }
        )

        binding.tracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
    }

    private fun setupBottomSheets() {
        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)
        
        binding.root.post {
            val screenHeight = binding.root.height
            val contentHeight = binding.playlistContentContainer.height
            val gap = dpToPx(24f)
            val peekHeight = screenHeight - contentHeight - gap

            if (peekHeight > 0) {
                tracksBottomSheetBehavior.peekHeight = peekHeight
            }
        }

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.visibility = if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.shareIcon.setOnClickListener {
            sharePlaylist()
        }

        binding.menuIcon.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.sharePlaylistMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.deletePlaylistMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }

        binding.editPlaylistMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_editPlaylistFragment,
                bundleOf("playlistId" to playlistId)
            )
        }
    }

    private fun renderPlaylist(playlist: Playlist) {
        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description
        binding.playlistDescription.visibility = if (playlist.description.isNullOrEmpty()) View.GONE else View.VISIBLE

        if (playlist.imagePath != null) {
            Glide.with(this)
                .load(File(playlist.imagePath))
                .placeholder(R.drawable.placeholder)
                .into(binding.playlistCoverBig)
        } else {
            binding.playlistCoverBig.setImageResource(R.drawable.placeholder)
        }

        // Small item in menu bottom sheet
        binding.playlistItemSmall.playlistName.text = playlist.name
        binding.playlistItemSmall.tracksCount.text = resources.getQuantityString(R.plurals.tracks_count, playlist.tracksCount, playlist.tracksCount)
        
        if (playlist.imagePath != null) {
            Glide.with(this)
                .load(File(playlist.imagePath))
                .placeholder(R.drawable.placeholder)
                .into(binding.playlistItemSmall.playlistCover)
        } else {
            binding.playlistItemSmall.playlistCover.setImageResource(R.drawable.placeholder)
        }
    }

    private fun renderTracks(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            binding.emptyPlaylistMessage.visibility = View.VISIBLE
            binding.tracksRecyclerView.visibility = View.GONE
        } else {
            binding.emptyPlaylistMessage.visibility = View.GONE
            binding.tracksRecyclerView.visibility = View.VISIBLE
            trackAdapter?.updateTracks(tracks)
        }

        val totalDurationMillis = tracks.sumOf { it.trackTimeMillis }
        val minutes = (totalDurationMillis / 1000 / 60).toInt()
        
        val durationText = resources.getQuantityString(R.plurals.minutes_count, minutes, minutes)
        val countText = resources.getQuantityString(R.plurals.tracks_count, tracks.size, tracks.size)
        
        binding.playlistInfo.text = getString(R.string.playlist_info_format, durationText, countText)
    }

    private fun sharePlaylist() {
        if (trackAdapter?.itemCount == 0) {
            Toast.makeText(requireContext(), R.string.playlist_is_empty_to_share, Toast.LENGTH_SHORT).show()
        } else {
            viewModel.sharePlaylist()
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
            .setMessage(R.string.delete_track_message)
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTrack(track)
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        val playlist = viewModel.observePlaylist().value ?: return
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
            .setMessage(getString(R.string.delete_playlist_message, playlist.name))
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deletePlaylist()
                findNavController().popBackStack()
            }
            .show()
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
