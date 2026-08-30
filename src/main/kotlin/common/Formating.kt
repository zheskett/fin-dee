package findee.common

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.*

val moneyFormat: NumberFormat = NumberFormat.getCurrencyInstance()

fun getDurationString(t1: OffsetDateTime?, t2: OffsetDateTime?): String {
    if (t1 == null || t2 == null) return "never"

    val dur = Duration.between(t1, t2)
    val tense = if (dur.isNegative) "from now" else "ago"
    val absDur = dur.abs()

    val timeMap = mapOf(
        "year" to absDur.toDays() / 365,
        "month" to absDur.toDays() / 30,
        "week" to absDur.toDays() / 7,
        "day" to absDur.toDays(),
        "hour" to absDur.toHours(),
        "minute" to absDur.toMinutes(),
        "second" to absDur.toSeconds()
    )

    for ((time, length) in timeMap) {
        if (length == 0.toLong()) continue

        return "$length ${pl(length, time)} $tense"
    }

    return "now"
}

fun calcTextColorClass(bgColor: String): String {
    val r = bgColor.substring(0, 2).hexToInt()
    val g = bgColor.substring(2, 4).hexToInt()
    val b = bgColor.substring(4, 6).hexToInt()
    val lum = (299 * r + 587 * g + 114 * b) / 1000

    return if (lum > 125) "has-text-grey-darker" else "has-text-white-ter"
}

private fun pl(x: Number, s: String): String {
    return if (x != 1) "${s}s" else s
}