package com.example.myapplogin

import android.icu.text.SimpleDateFormat
import android.text.format.DateUtils
import java.util.*

object TimestampUtils {
    fun convertTimestampToDay(timestamp: Long): String {
        val now = System.currentTimeMillis()
        return when {
            DateUtils.isToday(timestamp) -> {
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                timeFormat.format(Date(timestamp))
            }

            DateUtils.isToday(timestamp + DateUtils.DAY_IN_MILLIS) -> {
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                val timeString = timeFormat.format(Date(timestamp))
                "Yesterday, $timeString"
            }else -> {
                val dateFormat = SimpleDateFormat(" MMM d, yyyy", Locale.getDefault())
                dateFormat.format(Date(timestamp))
            }
        }
    }
}
