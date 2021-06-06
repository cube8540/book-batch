package cube8540.book.batch

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import cube8540.book.batch.external.kyobo.kr.KyoboAuthenticationInfo
import cube8540.book.batch.external.naver.com.NaverBookAPIKey
import cube8540.book.batch.external.nl.go.NationalLibraryAPIKey
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
class ObjectMapperConfiguration {

    @Bean
    @Primary
    fun defaultObjectMapper(): ObjectMapper {
        val timeModule = JavaTimeModule()
            .addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateTimeFormatter.ISO_DATE))
            .addSerializer(LocalDate::class.java, LocalDateSerializer(DateTimeFormatter.ISO_DATE))


        return ObjectMapper()
            .registerModule(timeModule)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
}

@ConstructorBinding
@ConfigurationProperties(prefix = "api.connection")
class APIConnectionProperty(
    val maxWaitSecond: Int? = 5,
    val retryCount: Int? = 1,
    val retryDelaySecond: Int? = 5
)

@ConstructorBinding
@ConfigurationProperties(prefix = "api.authentication")
class AuthenticationProperty(
    val nationalLibrary: NationalLibraryAPIKey,
    val naverBook: NaverBookAPIKey,
    val kyobo: KyoboAuthenticationInfo
)