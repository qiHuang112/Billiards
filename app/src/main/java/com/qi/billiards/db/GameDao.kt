package com.qi.billiards.db

import androidx.room.*

@Dao
interface GameDao {

    @Query("SELECT * FROM game")
    suspend fun getAllGames(): List<GameEntity>

    @Query("SELECT * FROM game WHERE game_type=:type")
    suspend fun getGamesByType(type: Int): List<GameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrUpdateGame(game: GameEntity): Long

    @Delete
    suspend fun deleteGame(gameEntity: GameEntity)
}