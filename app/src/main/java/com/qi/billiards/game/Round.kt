package com.qi.billiards.game

import java.io.Serializable
import java.util.*

/**
 * 一局游戏
 */
data class Round(
    val sequences: List<String>, // 玩家参与表
    val profits: Profits, // 单局收益表
    val during: During = During(Date()), // 单局时间
    var winner: String? = null, // 单局赢家名称
    val operators: MutableList<Operator> = mutableListOf(), // 单局操作表
) : Serializable {
    data class Profits(
        val totalProfits: List<Player>, // 单局总收益表
        val opProfits: MutableList<List<Player>> = mutableListOf(), // 单次操作收益变动表
    ) : Serializable
}

fun List<Player>.addOpProfit(operatorProfit: List<Player>, undo: Boolean = false) {
    val sign = if (undo) -1 else 1
    operatorProfit.forEach { player ->
        val target = find { it.name == player.name }
        if (target != null) {
            target.score = target.score + player.score * sign
        }
    }
}