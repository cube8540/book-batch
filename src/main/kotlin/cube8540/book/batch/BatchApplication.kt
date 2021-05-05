package cube8540.book.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.Clock
import java.time.ZoneOffset
import java.util.*

@SpringBootApplication
class BatchApplication {
    companion object {
        val DEFAULT_ZONE_OFFSET: ZoneOffset = ZoneOffset.of("+09:00")
        val DEFAULT_TIME_ZONE: TimeZone = TimeZone.getTimeZone("Asia/Seoul")
        val DEFAULT_CLOCK: Clock = Clock.system(DEFAULT_TIME_ZONE.toZoneId())
    }
}

fun main(args: Array<String>) {
    runApplication<BatchApplication>(*args)
}
