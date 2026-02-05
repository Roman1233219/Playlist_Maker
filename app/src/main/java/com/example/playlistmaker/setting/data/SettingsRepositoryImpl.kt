package com.example.playlistmaker.setting.data

import android.content.SharedPreferences
import com.example.playlistmaker.setting.domain.api.SettingsRepository
import com.example.playlistmaker.setting.domain.models.ThemeSettings

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(darkTheme = sharedPreferences.getBoolean(THEME_KEY, false))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, settings.darkTheme)
            .apply()
    }

    companion object {
        private const val THEME_KEY = "key_for_theme"
    }
}
