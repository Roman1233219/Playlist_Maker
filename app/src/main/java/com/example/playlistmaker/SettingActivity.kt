package com.example.playlistmaker

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.settings_activity)

        val backButton = findViewById<TextView>(R.id.button_back)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val themeSwitch = findViewById<SwitchCompat>(R.id.tema_button)

    }
}
