package com.qi.billiards.game

/**
 * 一局游戏
 */
data class OneGame(
    val sequenceList: List<String>, // 玩家参与表
    val operatorList: MutableList<Operator>, // 单局操作表
    val during: During, // 单局时间
)