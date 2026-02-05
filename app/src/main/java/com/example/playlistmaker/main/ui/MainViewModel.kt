package com.example.playlistmaker.main.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.setting.domain.SettingsInteractor
import com.example.playlistmaker.setting.domain.models.ThemeSettings

class MainViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val _themeSettings = MutableLiveData<ThemeSettings>()
    val themeSettings: LiveData<ThemeSettings> = _themeSettings

    fun loadTheme() {
        _themeSettings.value = settingsInteractor.getThemeSettings()
    }
}
