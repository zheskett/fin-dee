package findee.common

import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.time.delay
import java.time.*

private val LOGGER = KtorSimpleLogger("findee.common.Scheduler")

class Scheduler(val toRun: suspend () -> Unit) {
    private var job: Job = Job()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun scheduleAt(time: LocalTime, every: Duration) {
        runBlocking {
            this@Scheduler.cancel()
        }

        // Find closest time
        var nextTime = Duration.between(LocalTime.now(), time)
        while (nextTime.toMillis() < 0L) {
            nextTime = nextTime.plus(every)
        }

        job = scope.interval(nextTime, every)

        LOGGER.info("Scheduler started, next call in: $nextTime")
    }

    suspend fun cancel() {
        job.cancel()
        job.join()
    }

    private fun CoroutineScope.interval(initialInterval: Duration, regularInterval: Duration): Job {
        return launch {
            var isFirst = true
            while (isActive) {
                if (isFirst) {
                    delay(initialInterval)
                    isFirst = false
                } else {
                    delay(regularInterval)
                }
                try {
                    toRun()
                    LOGGER.info("Scheduler Ran")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    LOGGER.error(e.message)
                }
            }
        }
    }


}