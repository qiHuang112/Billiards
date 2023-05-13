package com.qi.billiards.db

import androidx.room.Room
import com.qi.billiards.AppContext

private val db by lazy {
    Room.databaseBuilder(AppContext, AppDatabase::class.java, "a.db").build()
}

object DbUtil : PlayerDao by db.playerDao(), GameDao by db.gameDao()