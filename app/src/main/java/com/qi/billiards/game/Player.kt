package com.qi.billiards.game

data class Player(
    val id: Int, // 玩家唯一id
    val name: String, // 玩家名称
    var score: Int, // 玩家分数
)
