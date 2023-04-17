package com.qi.billiards.game

/**
 * 一场游戏
 */
data class ZhongBaGame(
    val group: MutableList<Game>, // 对局列表
    val players: List<Player>, // 玩家
    val rule: Rule, // 规则
    val base: Int, // 基数
    val during: During, // 总时长
) {

    /**
     * 追分规则
     */
    data class Rule(
        val win: Int, // 普胜得分
        val jieqing: Int, // 接清得分
        val zhaqing: Int, // 炸清得分
    )

}

