package com.example.playlistmaker.setting.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.setting.domain.SettingsInteractor
import com.example.playlistmaker.setting.domain.models.ThemeSettings

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val _state = MutableLiveData<SettingsState>()
    val state: LiveData<SettingsState> get() = _state

    init {
        _state.value = SettingsState(settingsInteractor.getThemeSettings().darkTheme)
    }

    fun onThemeSwitch(isDark: Boolean) {
        settingsInteractor.updateThemeSetting(ThemeSettings(isDark))
        _state.value = SettingsState(isDark)
    }

    fun onShareApp() {
        settingsInteractor.shareApp()
    }

    fun onOpenSupport() {
        settingsInteractor.openSupport()
    }

    fun onOpenTerms() {
        settingsInteractor.openTerms()
    }
}
