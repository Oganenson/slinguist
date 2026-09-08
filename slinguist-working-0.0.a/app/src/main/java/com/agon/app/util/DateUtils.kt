package com.agon.app.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun relative(timestamp: Long?): String {
        if (timestamp == null) return "Нет активности"
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 60_000 -> "Только что"
            diff < 3600_000 -> "${diff / 60_000} мин. назад"
            diff < 86400_000 -> "${diff / 3600_000} ч. назад"
            else -> SimpleDateFormat("d MMM yyyy", Locale("ru")).format(Date(timestamp))
        }
    }

    fun plural(count: Int, one: String, few: String, many: String): String {
        val mod10 = count % 10
        val mod100 = count % 100
        return when {
            mod100 in 11..19 -> many
            mod10 == 1 -> one
            mod10 in 2..4 -> few
            else -> many
        }
    }
}
