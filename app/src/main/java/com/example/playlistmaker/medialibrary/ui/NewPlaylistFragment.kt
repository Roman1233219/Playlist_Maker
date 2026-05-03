package com.example.playlistmaker.medialibrary.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var imageUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.playlistCover.setImageURI(uri)
            imageUri = uri
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            handleBackNavigation()
        }

        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.createButton.isEnabled = !s.isNullOrBlank()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.playlistName.addTextChangedListener(textWatcher)

        binding.createButton.setOnClickListener {
            val name = binding.playlistName.text.toString()
            val description = binding.playlistDescription.text.toString()
            val imagePath = imageUri?.let { saveImageToInternalStorage(it) }

            viewModel.createPlaylist(name, description, imagePath)

            val message = getString(R.string.playlist_created, name)
            val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(requireContext().getColor(R.color.YP_Black))
            snackbar.setTextColor(requireContext().getColor(R.color.white))
            snackbar.show()

            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        })
    }

    private fun saveImageToInternalStorage(uri: Uri): String {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.absolutePath
    }

    private fun handleBackNavigation() {
        if (imageUri != null || !binding.playlistName.text.isNullOrBlank() || !binding.playlistDescription.text.isNullOrBlank()) {
            showConfirmDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun showConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.finish_playlist_creation_title)
            .setMessage(R.string.finish_playlist_creation_message)
            .setNeutralButton(R.string.cancel) { _, _ -> }
            .setPositiveButton(R.string.finish) { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
