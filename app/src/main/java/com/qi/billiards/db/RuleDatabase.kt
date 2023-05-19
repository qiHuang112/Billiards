package com.qi.billiards.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RuleEntity::class], version = 1)
abstract class RuleDatabase : RoomDatabase() {

    abstract fun ruleDao(): RuleDao
}