package com.qi.billiards.game

import java.io.Serializable

data class DeGame(
    var players: MutableList<DePlayer>,
    val configs: LinkedHashMap<String, Double>,
    val startTime: Long = System.currentTimeMillis(), // 开始时间
    var id: Long? = null,
) : Serializable