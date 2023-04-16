package com.qi.billiards.game

import com.qi.billiards.util.format
import java.util.Date

data class During(
    var startTime: Date? = null, // 开始时间
    var endTime: Date? = null, // 结束时间
) {
    fun getDuringTime() : String{
        return "[${startTime.format()}, ${endTime.format()}]"
    }
}
