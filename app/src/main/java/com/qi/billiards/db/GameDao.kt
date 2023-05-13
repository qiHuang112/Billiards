package com.qi.billiards.db

import androidx.room.*

@Dao
interface GameDao {

    @Query("SELECT * FROM game")
    fun getAllGames(): List<GameEntity>

    @Query("SELECT * FROM game WHERE game_type=:type")
    fun getGamesByType(type: Int): List<GameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateGame(game: GameEntity): Long

    @Delete
    fun deleteGame(gameEntity: GameEntity)
}