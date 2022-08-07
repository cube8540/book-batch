package cube8540.book.batch.translator.kyobo.kr.client

import feign.Logger
import feign.Retryer
import feign.codec.Decoder
import feign.codec.Encoder
import feign.form.FormEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class KyoboLoginClientConfiguration {

    @Bean
    fun kyoboLoginEncoder(): Encoder = FormEncoder()

    @Bean
    fun kyoboLoginDecoder(): Decoder = KyoboBookDecoder()

    @Bean
    fun kyoboBookClientRetry(
        @Value("\${api.connection.retry-count}") retryCount: Int,
        @Value("\${api.connection.retry-delay-second}") retryDelaySeconds: Long,
        @Value("\${api.connection.max-awit-second}") retryMaxAwaitSeconds: Long
    ): Retryer = Retryer.Default(retryDelaySeconds, retryMaxAwaitSeconds, retryCount)

    @Bean
    fun logging(): Logger.Level = Logger.Level.FULL
}