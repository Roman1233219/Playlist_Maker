package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [TrackEntity::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}
