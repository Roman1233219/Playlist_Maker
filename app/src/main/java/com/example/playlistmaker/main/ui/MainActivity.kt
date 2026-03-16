package com.example.playlistmaker.main.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.core.App
import com.example.playlistmaker.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.themeSettings.observe(this) { settings ->
            (application as App).applyTheme(settings.darkTheme)
        }

        viewModel.loadTheme()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateBottomNavVisibility(destination)
        }
    }

    private fun updateBottomNavVisibility(destination: NavDestination) {
        when (destination.id) {
            R.id.playerFragment -> {
                binding.bottomNavigation.visibility = View.GONE
            }
            else -> {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }
}