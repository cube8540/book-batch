package cube8540.book.batch.translator.kyobo.kr.client

import feign.Logger
import feign.RequestInterceptor
import feign.Retryer
import feign.codec.Decoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class KyoboBookClientConfiguration {

    @Bean
    fun kyoboBookClientDecoder(): Decoder = KyoboBookDecoder()

    @Bean
    fun kyoboBookClientRetry(
        @Value("\${api.connection.retry-count}") retryCount: Int,
        @Value("\${api.connection.retry-delay-second}") retryDelaySeconds: Long,
        @Value("\${api.connection.max-awit-second}") retryMaxAwaitSeconds: Long
    ): Retryer = Retryer.Default(retryDelaySeconds, retryMaxAwaitSeconds, retryCount)

    @Bean
    fun kyoboBookLoginInterceptor(
        @Value("\${api.authentication.kyobo.username}") username: String,
        @Value("\${api.authentication.kyobo.password}") password: String,
        @Autowired client: KyoboLoginClient
    ): RequestInterceptor = KyoboLoginInterceptor(username, password, client)

    @Bean
    fun logging(): Logger.Level = Logger.Level.FULL
}