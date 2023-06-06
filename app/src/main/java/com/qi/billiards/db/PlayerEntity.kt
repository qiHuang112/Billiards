package com.qi.billiards.db

import androidx.room.*

@Entity(tableName = "player")
data class PlayerEntity(
    @ColumnInfo(name = "player_name")
    val playerName: String,
    @ColumnInfo(name = "total_score")
    var totalScore: Double = 0.0,
    @ColumnInfo(name = "total_count")
    var totalCount: Long = 0,
    @ColumnInfo(name = "total_cost")
    var totalCost: Double = 0.0,
    @ColumnInfo(name = "win_count")
    var winCount: Long = 0,
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
) {
    fun getWinRate(): String {
        if (totalCount == 0L) {
            return "0.00%"
        }
        return "${String.format("%.2f", 100.0 * winCount / totalCount)}%"
    }
}
