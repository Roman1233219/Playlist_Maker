package com.example.playlistmaker.medialibrary.ui

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.setting.domain.SettingsInteractor

class MediaLibraryViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    fun applyCurrentTheme() {
        val settings = settingsInteractor.getThemeSettings()
        settingsInteractor.updateThemeSetting(settings)
    }
}