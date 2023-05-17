package com.qi.billiards.db

import androidx.room.*

@Dao
interface PlayerDao {

    @Query("SELECT * FROM player")
    suspend fun getAllPlayers(): List<PlayerEntity>

    @Query("SELECT * FROM player WHERE player_name=:name LIMIT 1")
    suspend fun getPlayerByName(name: String): PlayerEntity?

    @Query("SELECT * FROM player WHERE id=:id")
    suspend fun getPlayerById(id: Long): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlayer(player: PlayerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlayers(vararg players: PlayerEntity)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

}