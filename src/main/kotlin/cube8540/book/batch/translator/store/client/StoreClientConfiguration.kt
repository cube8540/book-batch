package cube8540.book.batch.translator.store.client

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.translator.store.OAuth2Provider
import feign.Logger
import feign.RequestInterceptor
import feign.Retryer
import feign.codec.Decoder
import feign.codec.Encoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder
import org.springframework.cloud.openfeign.support.SpringDecoder
import org.springframework.cloud.openfeign.support.SpringEncoder
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

class StoreClientConfiguration {

    companion object {
        const val APPLICATION_ID = "application"
    }

    @Bean
    fun storeClientEncoder(@Autowired @Qualifier("defaultObjectMapper") objectMapper: ObjectMapper): Encoder {
        val httpMessageConverter = MappingJackson2HttpMessageConverter(objectMapper)
        return SpringEncoder { HttpMessageConverters(httpMessageConverter) }
    }

    @Bean
    fun storeClientDecoder(@Autowired @Qualifier("defaultObjectMapper") objectMapper: ObjectMapper): Decoder {
        val httpMessageConverter = MappingJackson2HttpMessageConverter(objectMapper)
        return ResponseEntityDecoder(SpringDecoder { HttpMessageConverters(httpMessageConverter) })
    }

    @Bean
    fun storeClientAuthorizationInterceptor(@Autowired provider: OAuth2Provider): RequestInterceptor =
        StoreClientAuthorizationInterceptor(APPLICATION_ID, provider)

    @Bean
    fun storeClientRetry(
        @Value("\${api.connection.retry-count}") retryCount: Int,
        @Value("\${api.connection.retry-delay-second}") retryDelaySeconds: Long,
        @Value("\${api.connection.max-awit-second}") retryMaxAwaitSeconds: Long
    ): Retryer = Retryer.Default(retryDelaySeconds, retryMaxAwaitSeconds, retryCount)

    @Bean
    fun logging(): Logger.Level = Logger.Level.FULL
}