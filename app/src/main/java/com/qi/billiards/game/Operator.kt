package com.qi.billiards.game

import java.io.Serializable


/**
 * 一个操作
 */
data class Operator(
    val id: Int,
    val player: Player,
) : Serializable