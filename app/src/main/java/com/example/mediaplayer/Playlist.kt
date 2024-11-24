package com.example.mediaplayer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Уникальный идентификатор плейлиста
    val name: String, // Название плейлиста
    val description: String? = null // Описание плейлиста (опционально)
)
