package com.qi.billiards.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rule")
data class RuleEntity(
    @ColumnInfo(name = "game_type")
    val gameType: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "edit_time")
    val editTime: Long,
    @ColumnInfo(name = "rule_id")
    @PrimaryKey(autoGenerate = true)
    val ruleId: Long? = null
)