package cube8540.book.batch

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling
import java.time.Clock
import java.time.ZoneOffset
import java.util.*

@SpringBootApplication
@EnableRetry
@EnableBatchProcessing
@EnableBatchIntegration
@EnableScheduling
@EnableFeignClients
@EnableConfigurationProperties(value = [ AuthenticationProperty::class ])
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
