package com.qi.billiards.db

import androidx.room.*

@Entity(tableName = "game")
data class GameEntity(
    @ColumnInfo(name = "game_type")
    val gameType: Int,
    @ColumnInfo(name = "start_time")
    val startTime: Long?,
    @ColumnInfo(name = "end_time")
    val endTime: Long?,
    val detail: String,
    @ColumnInfo(name = "game_id")
    @PrimaryKey(autoGenerate = true)
    val gameId: Long? = null,
)
