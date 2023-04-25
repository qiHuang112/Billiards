package com.qi.billiards.game

import com.qi.billiards.util.format
import com.qi.billiards.util.toStandardTime
import java.util.Date

data class During(
    var startTime: Date? = null, // 开始时间
    var endTime: Date? = null, // 结束时间
) {
    fun getDuringTime() : String{
        return "[${startTime.format()}, ${endTime.format()}]"
    }

    fun getCostTime():String {
        if (startTime == null || endTime == null) {
            return "进行中..."
        }
        val costTime = endTime!!.time - startTime!!.time
        return "耗时：${costTime.toStandardTime()}"
    }
}
