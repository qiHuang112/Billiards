package com.qi.billiards.db

import androidx.room.*

@Entity(tableName = "player")
data class PlayerEntity(
    @ColumnInfo(name = "player_name")
    val playerName: String,
    @ColumnInfo(name = "total_score")
    val totalScore: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
)
