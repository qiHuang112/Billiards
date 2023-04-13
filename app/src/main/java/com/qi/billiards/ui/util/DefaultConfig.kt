package com.qi.billiards.ui.util

class DefaultConfig {
    class ZhuiFen {

        companion object {
            fun get(name: String) = map.getOrDefault(name, "")

            private val map = listOf(
                "初始分数" to "25",
                "犯规罚分" to "1",
                "普胜得分" to "4",
                "小金得分" to "7",
                "大金得分" to "7",
                "基数" to "5",
            ).toMap()

            const val SCORE_FOUL = 1
            const val SCORE_WIN = 4
            const val SCORE_XIAOJIN = 7
            const val SCORE_DAJIN = 7
        }

    }

}