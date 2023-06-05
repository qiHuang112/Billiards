package com.qi.billiards.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlayerEntity::class, GameEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
}