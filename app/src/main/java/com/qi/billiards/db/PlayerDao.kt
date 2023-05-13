package com.qi.billiards.db

import androidx.room.*

@Dao
interface PlayerDao {

    @Query("SELECT * FROM player")
    fun getAllPlayers(): List<PlayerEntity>

    @Query("SELECT * FROM player WHERE player_name=:name LIMIT 1")
    fun getPlayerByName(name: String): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlayer(player: PlayerEntity): Long

    @Delete
    fun deletePlayer(player: PlayerEntity)

}