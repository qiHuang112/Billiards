package com.qi.billiards.config

import com.qi.billiards.ui.main.zhuifen.start.OperatorGridAdapter

class DefaultConfig {

    object ZhuiFen {
        fun get(name: String) = map.getOrDefault(name, 0)

        private val map = mapOf(
            "初始分数" to 25,
            "犯规罚分" to 1,
            "普胜得分" to 4,
            "小金得分" to 7,
            "大金得分" to 7,
            "基数" to 5,
        )

        val userOperators = mutableListOf(
            OperatorGridAdapter.UserOperator("自然犯规", 0),
            OperatorGridAdapter.UserOperator("解球犯规", 1),
            OperatorGridAdapter.UserOperator("自然普胜", 2),
            OperatorGridAdapter.UserOperator("解球普胜", 3),
            OperatorGridAdapter.UserOperator("自然小金", 4),
            OperatorGridAdapter.UserOperator("解球小金", 5),
            OperatorGridAdapter.UserOperator("大金", 6),
            OperatorGridAdapter.UserOperator("撤回", 7)
        )
    }

}
/*
自然犯规
解球犯规
自然普胜
解球普胜
自然小金
解球小金
大金
撤回
 */