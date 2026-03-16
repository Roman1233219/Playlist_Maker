package com.example.playlistmaker.setting.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.core.App
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.temaButton.isChecked = state.isDarkTheme
            // Применяем тему через метод в App
            (requireContext().applicationContext as App).applyTheme(state.isDarkTheme)
        }

        binding.temaButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitch(isChecked)
        }

        binding.shareButton.setOnClickListener { viewModel.onShareApp() }
        binding.helpButton.setOnClickListener { viewModel.onOpenSupport() }
        binding.strelRButton.setOnClickListener { viewModel.onOpenTerms() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}