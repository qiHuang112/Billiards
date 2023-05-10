package com.qi.billiards.game

import java.io.Serializable

data class Player(
    val name: String, // 玩家名称
    var score: Int, // 玩家分数
) : Serializable
