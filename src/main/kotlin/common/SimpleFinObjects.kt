package findee.common

import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class SimpleFinAccount(val id: String, val name: String, @SerialName("conn_id") val connId: String)