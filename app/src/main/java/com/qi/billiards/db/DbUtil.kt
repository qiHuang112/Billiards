package com.qi.billiards.db

import androidx.room.Room
import com.qi.billiards.AppContext

private val gameDb by lazy {
    Room.databaseBuilder(AppContext, GameDatabase::class.java, "a.db").build()
}

object DbUtil : PlayerDao by gameDb.playerDao(), GameDao by gameDb.gameDao()