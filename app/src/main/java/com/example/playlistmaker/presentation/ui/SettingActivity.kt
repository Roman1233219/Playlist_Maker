package com.example.playlistmaker.presentation.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.App
import com.example.playlistmaker.R

class SettingActivity : AppCompatActivity() {
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

        val app = applicationContext as App
        themeSwitch.isChecked = app.darkTheme

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).applyTheme(isChecked)

            val sharedPrefs = getSharedPreferences(App.PREFERENCES, MODE_PRIVATE)
            sharedPrefs.edit()
                .putBoolean(App.THEME_KEY, isChecked)
                .apply()
        }

        val shareButton = findViewById<TextView>(R.id.share_button)
        shareButton.setOnClickListener { shareApp() }

        val helpButton = findViewById<TextView>(R.id.help_button)
        helpButton.setOnClickListener { contactSupport() }

        val termsButton = findViewById<TextView>(R.id.strel_r_button)
        termsButton.setOnClickListener { openTerms() }
    }

    private fun shareApp() {
        try {
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.share_course_text, getString(R.string.android_course_url)))
                },
                getString(R.string.share_dialog_title)
            ).also { startActivity(it) }
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.no_apps_found)
        }
    }

    private fun contactSupport() {
        try {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
            }.also { startActivity(it) }
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.no_email_client)
        }
    }

    private fun openTerms() {
        try {
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_url)))
                .also { startActivity(it) }
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.no_browser_found)
        }
    }

    private fun showToast(messageRes: Int) {
        Toast.makeText(this, getString(messageRes), Toast.LENGTH_SHORT).show()
    }
}
