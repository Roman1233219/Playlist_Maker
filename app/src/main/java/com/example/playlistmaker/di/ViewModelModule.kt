package com.example.playlistmaker.di

import com.example.playlistmaker.main.ui.MainViewModel
import com.example.playlistmaker.medialibrary.ui.FavoriteTracksViewModel
import com.example.playlistmaker.medialibrary.ui.MediaLibraryViewModel
import com.example.playlistmaker.medialibrary.ui.NewPlaylistViewModel
import com.example.playlistmaker.medialibrary.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.setting.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MainViewModel(get())
    }
    viewModel {
        MediaLibraryViewModel(get())
    }
    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get(), get())
    }
    viewModel {
        SearchViewModel(get(), get())
    }
    viewModel {
        SettingsViewModel(get())
    }
    viewModel {
        FavoriteTracksViewModel(get())
    }
    viewModel {
        PlaylistsViewModel(get())
    }
    viewModel {
        NewPlaylistViewModel(get())
    }
}
