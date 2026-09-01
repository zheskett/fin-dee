package findee.backend

import findee.common.SimpleFinAccountSet
import findee.db.*
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.ApplicationConfig
import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.json.Json
import java.time.OffsetDateTime

val client = HttpClient(CIO) {
    val config = ApplicationConfig("application.yaml")
    val simpleFinURL = config.property("ktor.client.simplefin_url").getString()
    val simpleFinUsername = config.property("ktor.client.simplefin_username").getString()
    val simpleFinPass = config.property("ktor.client.simplefin_password").getString()

    install(Logging) {
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 15_000
        requestTimeoutMillis = 60_000
        socketTimeoutMillis = 60_000
    }
    engine {
        requestTimeout = 0
    }

    defaultRequest {
        url(simpleFinURL)
        basicAuth(simpleFinUsername, simpleFinPass)
    }

    expectSuccess = true
}

suspend fun updateSimpleFin(
    startDate: OffsetDateTime? = null,
    balancesOnly: Boolean = true,
    pending: Boolean = true
): Boolean {
    val res = try {
        client.get("accounts") {
            url {
                parameters.append("version", "2")
                parameters.append("balances-only", if (balancesOnly) "1" else "0")
                parameters.append("pending", if (pending) "1" else "0")
                val epochLong = startDate?.toEpochSecond() ?: OffsetDateTime.now().toEpochSecond()
                parameters.append("start-date", epochLong.toString())
            }
        }
    } catch (e: ResponseException) {
        storeUpdateError(e.message, e.response.status)
        return false
    }

    val accountSet: SimpleFinAccountSet = res.body()
    return storeUpdate(accountSet, res.status)
}