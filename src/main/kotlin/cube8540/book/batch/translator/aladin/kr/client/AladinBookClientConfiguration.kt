package cube8540.book.batch.translator.aladin.kr.client

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import feign.Logger
import feign.QueryMapEncoder
import feign.Retryer
import feign.codec.Decoder
import feign.codec.Encoder
import feign.querymap.BeanQueryMapEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder
import org.springframework.cloud.openfeign.support.SpringDecoder
import org.springframework.cloud.openfeign.support.SpringEncoder
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AladinBookClientConfiguration {

    @Bean
    fun aladinBookClientQueryMapEncoder(): QueryMapEncoder = BeanQueryMapEncoder()

    @Bean
    fun aladinBookClientEncoder(): Encoder {
        val objectMapper = ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)

        val httpMessageConverter = MappingJackson2HttpMessageConverter(objectMapper)
        return SpringEncoder { HttpMessageConverters(httpMessageConverter) }
    }

    @Bean
    fun aladinBookClientDecoder(): Decoder {
        val timeModule = JavaTimeModule()
            .addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateTimeFormatter.ISO_DATE))

        val objectMapper = ObjectMapper()
            .registerModule(timeModule)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        val httpMessageConverter = MappingJackson2HttpMessageConverter(objectMapper)
        return ResponseEntityDecoder(SpringDecoder { HttpMessageConverters(httpMessageConverter) })
    }

    @Bean
    fun aladinBookClientRetry(
        @Value("\${api.connection.retry-count}") retryCount: Int,
        @Value("\${api.connection.retry-delay-second}") retryDelaySeconds: Long,
        @Value("\${api.connection.max-awit-second}") retryMaxAwaitSeconds: Long
    ): Retryer = Retryer.Default(retryDelaySeconds, retryMaxAwaitSeconds, retryCount)

    @Bean
    fun logging(): Logger.Level = Logger.Level.FULL
}