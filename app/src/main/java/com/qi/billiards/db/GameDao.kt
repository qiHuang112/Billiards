package com.qi.billiards.db

import androidx.room.*

@Dao
interface GameDao {

    @Query("SELECT * FROM game")
    suspend fun getAllGames(): List<GameEntity>

    @Query("SELECT * FROM game ORDER BY game_id DESC")
    suspend fun getAllGamesDESC(): List<GameEntity>

    @Query("SELECT * FROM game WHERE game_id=:id")
    suspend fun getGameById(id: Long): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrUpdateGame(game: GameEntity): Long

    @Delete
    suspend fun deleteGame(gameEntity: GameEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGames(vararg games: GameEntity)

    @Query("DELETE FROM game")
    suspend fun deleteAllGames()
}