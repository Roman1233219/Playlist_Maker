package com.example.playlistmaker.setting.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.core.App
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings_activity)

        val settingsContainer = findViewById<View>(R.id.settings_container)
        ViewCompat.setOnApplyWindowInsetsListener(settingsContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<TextView>(R.id.button_back)
        backButton.setOnClickListener { 
            onBackPressedDispatcher.onBackPressed()
        }

        val themeSwitch = findViewById<SwitchCompat>(R.id.tema_button)

        viewModel.state.observe(this) { state ->
            themeSwitch.isChecked = state.isDarkTheme
            (applicationContext as App).applyTheme(state.isDarkTheme)
        }

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitch(isChecked)
        }

        val shareButton = findViewById<TextView>(R.id.share_button)
        shareButton.setOnClickListener { viewModel.onShareApp() }

        val helpButton = findViewById<TextView>(R.id.help_button)
        helpButton.setOnClickListener { viewModel.onOpenSupport() }

        val termsButton = findViewById<TextView>(R.id.strel_r_button)
        termsButton.setOnClickListener { viewModel.onOpenTerms() }
    }
}