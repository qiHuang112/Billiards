package com.qi.billiards.game

import java.io.Serializable

data class DePlayer(
    var name: String,
    var score: Double, // 当前分数
    var cost: Double = 0.0, // 台费
    var buyCount: Int = 0, // 买入次数
    var size: Int = 1, // AA台费
    var profit: Double = 0.0, // 盈亏
    val id: Long? = null,
) : Serializable
