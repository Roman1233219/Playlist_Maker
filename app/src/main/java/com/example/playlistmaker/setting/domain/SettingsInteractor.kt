package com.example.playlistmaker.setting.domain

import com.example.playlistmaker.setting.domain.models.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
    fun shareApp()
    fun openSupport()
    fun openTerms()
}
