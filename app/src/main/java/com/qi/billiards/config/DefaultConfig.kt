package com.qi.billiards.config

class DefaultConfig {
    class ZhuiFen {

        companion object {
            fun get(name: String) = map.getOrDefault(name, 0)

            private val map = mapOf(
                "初始分数" to 25,
                "犯规罚分" to 1,
                "普胜得分" to 4,
                "小金得分" to 7,
                "大金得分" to 7,
                "基数" to 5,
            )
        }

    }

}