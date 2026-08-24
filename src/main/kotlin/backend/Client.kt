package findee.backend

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.ApplicationConfig
import kotlinx.serialization.json.Json
import java.time.OffsetDateTime

val client = HttpClient(CIO) {
    install(Logging)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }


    defaultRequest {
        val config = ApplicationConfig("application.yaml")
        val simpleFinURL = config.property("ktor.client.simplefin_url").getString()
        url(simpleFinURL)
    }

    expectSuccess = true
}

suspend fun updateSimpleFin(startDate: OffsetDateTime? = null, balancesOnly: Boolean = true, pending: Boolean = true) {
    val res = client.get("accounts") {
        url {
            parameters.append("version", "2")
            parameters.append("balances-only", if (balancesOnly) "1" else "0")
            parameters.append("pending", if (pending) "1" else "0")
            val epochLong = startDate?.toEpochSecond() ?: OffsetDateTime.now().toEpochSecond()
            parameters.append("start-date", epochLong.toString())
        }
    }
}