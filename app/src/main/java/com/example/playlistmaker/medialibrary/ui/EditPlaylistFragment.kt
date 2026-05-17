package com.example.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import java.io.File

class EditPlaylistFragment : NewPlaylistFragment() {

    private var playlistId: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = arguments?.getInt("playlistId") ?: 0

        binding.toolbar.title = getString(R.string.edit)
        binding.createButton.text = getString(R.string.save)

        viewModel.getPlaylist(playlistId)

        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            binding.playlistName.setText(playlist.name)
            binding.playlistDescription.setText(playlist.description)
            if (playlist.imagePath != null) {
                Glide.with(this)
                    .load(File(playlist.imagePath))
                    .placeholder(R.drawable.placeholder)
                    .into(binding.playlistCover)
            }
        }

        binding.createButton.setOnClickListener {
            val name = binding.playlistName.text.toString()
            val description = binding.playlistDescription.text.toString()
            
            // Если imageUri не null, значит пользователь выбрал новую картинку
            // Если null - оставляем старую
            val imagePath = imageUri?.let { saveImageToInternalStorage(it) } ?: viewModel.observePlaylist().value?.imagePath

            val updatedPlaylist = viewModel.observePlaylist().value?.copy(
                name = name,
                description = description,
                imagePath = imagePath
            )

            if (updatedPlaylist != null) {
                viewModel.updatePlaylist(updatedPlaylist)
                findNavController().popBackStack()
            }
        }
    }

    override fun handleBackNavigation() {
        findNavController().popBackStack()
    }
}
