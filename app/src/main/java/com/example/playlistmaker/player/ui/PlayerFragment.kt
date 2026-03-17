package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

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

        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPlayPause.setOnClickListener {
            viewModel.playbackControl()
        }

        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            render(state)
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