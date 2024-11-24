package com.example.mediaplayer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Playlist::class, Track::class], version = 3, exportSchema = true)
abstract class MediaPlayerDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: MediaPlayerDatabase? = null

        fun getInstance(context: Context): MediaPlayerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MediaPlayerDatabase::class.java,
                    "media_player_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
