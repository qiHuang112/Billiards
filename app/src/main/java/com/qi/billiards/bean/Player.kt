package com.qi.billiards.bean

data class Player(
    var name: String,
    var buyCount: Int = 0, // 买入次数
    var cost: Double = 0.0, // 台费
    var profit: Double = 0.0, // 盈亏
    var score: Double, // 当前分数
    var aa: Int = 1, // AA台费
) : java.io.Serializable