package com.qi.billiards.game

/**
 * 一场游戏
 */
data class ZhuiFenGame(
    val gameList: MutableList<OneGame>, // 对局列表
    val playerList: List<Player>, // 玩家
    val rule: Rule, // 规则
    val base: Int, // 基数
    val profit: Int, // 盈亏
    val during: During, // 总时长
) {

    /**
     * 追分规则
     */
    data class Rule(
        val foul: Int, // 犯规罚分
        val win: Int, // 普胜得分
        val xiaojin: Int, // 小金得分
        val dajin: Int, // 大金得分
    )
}

