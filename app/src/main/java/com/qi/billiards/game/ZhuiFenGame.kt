package com.qi.billiards.game

import java.io.Serializable

/**
 * 一场游戏
 */
data class ZhuiFenGame(
    val group: MutableList<Game>, // 对局列表
    val players: List<Player>, // 玩家
    val rule: Rule, // 规则
    val base: Int, // 基数
    val during: During, // 总时长
    val summaries: List<Summary>, // 所有玩家总结
) : Serializable {

    /**
     * 追分规则
     */
    data class Rule(
        val foul: Int, // 犯规罚分
        val win: Int, // 普胜得分
        val xiaojin: Int, // 小金得分
        val dajin: Int, // 大金得分
    )

    data class Summary(
        val name: String,
        var foulCount: Int = 0,
        var winCount: Int = 0,
        var xiaojinCount: Int = 0,
        var dajinCount: Int = 0,
    )
}

