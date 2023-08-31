package com.qi.billiards.bean

import com.qi.billiards.data.AppData


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

    fun getLast5Profit(key:String): String {
        val games = AppData.globalGames[key] ?: return ""
        var profit = 0.0
        var count = 0
        for (i in games.size - 1 downTo 0) {
            val game = games[i]
            if (game.players.any { it.name == name }) {
                profit += game.players.first { it.name == name }.profit
                count++
            } else {
                continue
            }
            if (count == 5) {
                break
            }
        }
        return String.format("%.1f", profit)
    }
}

