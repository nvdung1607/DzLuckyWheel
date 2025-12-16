package com.example.dzluckywheel.utils

import com.example.dzluckywheel.data.model.Entry

object RandomUtils {
    fun getTargetAngle(entries: List<Entry>): Float {
        val validEntries = entries.filter { !it.excluded }
        if (validEntries.isEmpty()) return 0f

        val index = (validEntries.indices).random()
        val sweepAngle = 360f / entries.size
        return index * sweepAngle + sweepAngle / 2
    }
}
