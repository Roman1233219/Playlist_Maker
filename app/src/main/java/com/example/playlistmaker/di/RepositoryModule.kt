package com.example.playlistmaker.di

import com.example.playlistmaker.medialibrary.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.medialibrary.data.PlaylistDbConvertor
import com.example.playlistmaker.medialibrary.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.medialibrary.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.medialibrary.domain.api.PlaylistsRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    factory { PlaylistDbConvertor(get()) }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get())
    }
}
