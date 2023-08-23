package com.qi.billiards.bean


data class GlobalPlayer(
    var name: String,
    var totalProfit: Double = 0.0, // 总盈亏
    var totalCount: Long = 0, // 总局数
    var totalCost: Double = 0.0,  // 总台费
    var winCount: Long = 0, // 胜利局数
) {
    fun getWinRate(): String {
        if (totalCount == 0L) {
            return "0.00%"
        }
        return "${String.format("%.2f", 100.0 * winCount / totalCount)}%"
    }
}

