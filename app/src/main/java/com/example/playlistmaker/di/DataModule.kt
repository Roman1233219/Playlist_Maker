package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.TrackDbConvertor
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.setting.data.SettingsRepositoryImpl
import com.example.playlistmaker.setting.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
    }

    single {
        Gson()
    }

    single<ExternalNavigator> {
        ExternalNavigator(androidContext())
    }

    factory {
        MediaPlayer()
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get(), get())
    }

    factory<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory<SharingRepository> {
        SharingRepositoryImpl(androidContext(), get())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single { get<AppDatabase>().trackDao() }

    factory { TrackDbConvertor() }
}
