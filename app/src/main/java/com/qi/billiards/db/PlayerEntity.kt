package com.qi.billiards.db

import androidx.room.*

@Entity(tableName = "player")
data class PlayerEntity(
    @ColumnInfo(name = "player_name")
    val playerName: String,
    @ColumnInfo(name = "total_score")
    var totalScore: Long,
    @ColumnInfo(name = "total_count")
    var totalCount: Long,
    @ColumnInfo(name = "win_count")
    var winCount: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
)
