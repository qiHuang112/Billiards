package com.qi.billiards.game

import com.google.gson.Gson
import com.qi.billiards.config.Config
import com.qi.billiards.db.GameEntity
import java.io.Serializable

/**
 * 一场游戏
 */
data class ZhuiFenGame(
    val group: MutableList<Round>, // 对局列表
    val players: List<Player>, // 玩家
    val rule: Rule, // 规则
    val base: Int, // 基数
    val during: During, // 总时长
    val summaries: LinkedHashMap<String, MutableMap<String, Int>>, // 所有玩家总结
    var id: Long? = null
) : Serializable {
    fun toEntity() = GameEntity(
        Config.TYPE_ZHUI_FEN,
        during.startTime?.time,
        during.endTime?.time,
        Gson().toJson(this),
        id
    )

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

    /**
     * 追分规则
     */
    data class Rule(
        val foul: Int, // 犯规罚分
        val win: Int, // 普胜得分
        val xiaojin: Int, // 小金得分
        val dajin: Int, // 大金得分
    ) : Serializable
}

