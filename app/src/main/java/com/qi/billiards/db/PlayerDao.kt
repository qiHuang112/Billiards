package com.qi.billiards.db

import androidx.room.*

@Dao
interface PlayerDao {

    @Query("SELECT * FROM player")
    suspend fun getAllPlayers(): List<PlayerEntity>

    @Query("SELECT * FROM player WHERE player_name=:name LIMIT 1")
    suspend fun getPlayerByName(name: String): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlayer(player: PlayerEntity): Long

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

}