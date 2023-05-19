package com.qi.billiards.db

import androidx.room.*

@Dao
interface RuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRule(rule: RuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRules(vararg rules: RuleEntity)

    @Query("SELECT * FROM rule WHERE game_type=:type")
    suspend fun getAllRules(type: String): List<RuleEntity>

    @Delete
    suspend fun removeRules(vararg rules: RuleEntity)

    @Query("SELECT * FROM rule WHERE game_type = :type AND content LIKE '%' || :key || '%'")
    suspend fun getRulesByKey(type: String, key: String): List<RuleEntity>
}