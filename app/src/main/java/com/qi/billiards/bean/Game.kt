package com.qi.billiards.bean

import com.qi.billiards.util.format
import java.io.Serializable
import java.util.Date

data class Game(
    val configs: LinkedHashMap<String, Double>,
    var players: MutableList<Player>,
    val type: String,
    val date: String = Date().format("yyyy-MM-dd HH:mm:ss"),
) : Serializable