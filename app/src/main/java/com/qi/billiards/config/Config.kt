package com.qi.billiards.config

import com.qi.billiards.ui.main.zhuifen.start.OperatorGridAdapter

object Config {

    const val TYPE_ZHUI_FEN = 1
    const val TYPE_ZHONG_BA = 2

    val gameType = mapOf(
        TYPE_ZHUI_FEN to "追分",
        TYPE_ZHONG_BA to "中八",
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
            OperatorGridAdapter.UserOperator(opMap.getOrDefault(it, "[未定义操作]"), it, opColorMap[it])
        }
    }

}