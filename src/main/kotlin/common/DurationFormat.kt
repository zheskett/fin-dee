package findee.common

import java.time.Duration
import java.time.OffsetDateTime

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

private fun pl(x: Number, s: String): String {
    return if (x != 1) "${s}s" else s
}