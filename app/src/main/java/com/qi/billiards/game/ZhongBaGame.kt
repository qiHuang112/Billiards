package com.qi.billiards.game

import com.google.gson.Gson
import com.qi.billiards.config.Config
import com.qi.billiards.db.GameEntity
import java.io.Serializable
import java.util.*

/**
 * 一场游戏
 */
data class ZhongBaGame(
    val group: MutableList<Round>, // 对局列表
    val players: List<Player>, // 玩家
    val summaries: LinkedHashMap<String, MutableMap<String, Int>>, // 所有玩家总结
    val rule: Rule = Rule(1, 1, 1), // 规则
    val base: Int = 5, // 基数
    val during: During = During(Date()), // 总时长
    var id: Long? = null
) : Serializable {


    /**
     * 拿到当轮游戏的玩家分数记录表
     */
    fun getRoundPlayers() = players.map { player ->
        player.copy(
            name = player.name,
            score = 0,
            id = player.id
        )
    }

    fun toEntity() = GameEntity(
        Config.TYPE_ZHONG_BA,
        during.startTime?.time,
        during.endTime?.time,
        Gson().toJson(this),
        id
    )

    /**
     * 追分规则
     */
    data class Rule(
        val win: Int, // 普胜得分
        val zhaqing: Int, // 炸清得分
        val jieqing: Int, // 接清得分
    ) : Serializable

}

