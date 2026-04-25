package com.example.playlistmaker.di

import com.example.playlistmaker.medialibrary.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.medialibrary.domain.api.FavoriteTracksRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }
}
