package com.qi.billiards.game

/**
 * 一局游戏
 */
data class Game(
    val sequences: List<String>, // 玩家参与表
    val operators: MutableList<Operator>, // 单局操作表
    val profits: Profits, // 单局收益表
    val during: During, // 单局时间
    var winner: Player? = null,
) {
    data class Profits(
        val totalProfits: List<Player>, // 单局总收益表
        val opProfits: MutableList<List<Player>>, // 单次操作收益变动表
    )
}

fun List<Player>.addOpProfit(operatorProfit: List<Player>) {
    operatorProfit.forEach { player ->
        val target = find { it.name == player.name }
        if (target != null) {
            target.score += player.score
        }
    }
}

fun List<Player>.removeOpProfit(operatorProfit: List<Player>) {
    operatorProfit.forEach { player ->
        val target = find { it.name == player.name }
        if (target != null) {
            target.score -= player.score
        }
    }
}