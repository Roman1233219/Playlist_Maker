package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // Включаем edge-to-edge
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

    }
}
