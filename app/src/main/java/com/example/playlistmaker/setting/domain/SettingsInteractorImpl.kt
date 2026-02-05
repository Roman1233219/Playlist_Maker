package com.example.playlistmaker.setting.domain

import com.example.playlistmaker.setting.domain.api.SettingsRepository
import com.example.playlistmaker.setting.domain.models.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository,
    private val sharingRepository: SharingRepository
) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingsRepository.updateThemeSetting(settings)
    }

    override fun shareApp() {
        sharingRepository.shareApp()
    }

    override fun openSupport() {
        sharingRepository.openSupport()
    }

    override fun openTerms() {
        sharingRepository.openTerms()
    }
}
