package com.example.mediaplayer

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    // Добавление нового плейлиста
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist)

    // Добавление нового трека
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track)

    // Получение всех плейлистов
    @Query("SELECT * FROM playlist")
    fun getAllPlaylists(): Flow<List<Playlist>>

    // Получение всех треков по ID плейлиста
    @Query("SELECT * FROM track WHERE id = :trackId LIMIT 1")
    fun getTrackById(trackId: Int): Flow<Track>

    // Удаление плейлиста по ID
    @Query("DELETE FROM playlist WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Int)

    // Удаление всех плейлистов
    @Query("DELETE FROM playlist")
    suspend fun deleteAllPlaylists()

    // Удаление трека по ID
    @Query("DELETE FROM track WHERE id = :trackId")
    suspend fun deleteTrackById(trackId: Int)
}
