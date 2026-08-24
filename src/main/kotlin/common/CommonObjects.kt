package findee.common

import java.time.OffsetDateTime

data class Update(
    val id: Int,
    val httpCode: Int,
    val createdAt: OffsetDateTime
)