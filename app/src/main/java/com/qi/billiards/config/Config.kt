package com.qi.billiards.config

import com.qi.billiards.ui.main.UserOperator

object Config {

    const val TYPE_ZHUI_FEN = 1
    const val TYPE_ZHONG_BA = 2
    const val TYPE_DE = 3

    val gameType = mapOf(
        TYPE_ZHUI_FEN to "追分",
        TYPE_ZHONG_BA to "中八",
        TYPE_DE to "德",
    )

    object ZhuiFen {

        val ruleString = listOf(
            "初始分数",
            "犯规罚分",
            "普胜得分",
            "小金得分",
            "大金得分",
            "基数",
        )

        const val OP_0 = 0 // 自然犯规
        const val OP_1 = 1 // 解球犯规
        const val OP_2 = 2 // 自然普胜
        const val OP_3 = 3 // 解球普胜
        const val OP_4 = 4 // 自然小金
        const val OP_5 = 5 // 解球小金
        const val OP_6 = 6 // 大金
        const val OP_7 = 7 // 撤回

        val opMap = linkedMapOf(
            OP_0 to "自然犯规",
            OP_1 to "解球犯规",
            OP_2 to "自然普胜",
            OP_3 to "解球普胜",
            OP_4 to "自然小金",
            OP_5 to "解球小金",
            OP_6 to "大金",
            OP_7 to "撤回",
        )

        val opColorMap = linkedMapOf(
            OP_0 to "#FF5722",
            OP_1 to "#FF5722",
            OP_2 to "#4CAF50",
            OP_3 to "#4CAF50",
            OP_4 to "#FFC107",
            OP_5 to "#FFC107",
            OP_6 to "#FFD700",
            OP_7 to "#2196F3",
        )

        val userOperators = MutableList(opMap.size) {
            UserOperator(opMap.getOrDefault(it, "[未定义操作]"), it, opColorMap[it])
        }

        val rules = listOf(
            "赢得比赛：打进九号球且无犯规，算赢得比赛",
            "扣分顺序：在没有要求解球的情况下，当前玩家得分，上家扣分。否则当前玩家得分，下家扣分，犯规罚分逻辑一样",
            "小金：场上有1有9，只要打进9号，且无犯规，就算小金",
            "大金：开球后清台，且无犯规，算大金",
            "输球：自由球进了9号，或者正常击打时9号进球且犯规了，不仅算输球，也算犯规，犯规罚分给上家，下家得一个普胜",
            "争议球：肉眼无法分辨是否犯规，算好球",
        )
    }

    object ZhongBa {

        val ruleString = listOf(
            "初始分数",
            "普胜得分",
            "炸清得分",
            "接清得分",
            "基数",
        )

        const val OP_0 = 0 // 普胜
        const val OP_1 = 1 // 炸请
        const val OP_2 = 2 // 接清
        const val OP_3 = 3 // 撤回

        val opMap = linkedMapOf(
            OP_0 to "普胜",
            OP_1 to "炸请",
            OP_2 to "接清",
            OP_3 to "撤回",
        )

        val opColorMap = linkedMapOf(
            OP_0 to "#4CAF50",
            OP_1 to "#FFD700",
            OP_2 to "#FFC107",
            OP_3 to "#2196F3",
        )

        val userOperators = MutableList(opMap.size) {
            UserOperator(opMap.getOrDefault(it, "[未定义操作]"), it, opColorMap[it])
        }

        val rules = listOf(
            "争议球：肉眼无法分辨是否犯规，算好球",
        )
    }

}