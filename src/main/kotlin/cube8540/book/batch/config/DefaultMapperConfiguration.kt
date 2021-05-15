package cube8540.book.batch.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import cube8540.book.batch.domain.DivisionRawMapper
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIErrorResponse
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapper
import cube8540.book.batch.infra.naver.com.NaverBookAPIDeserializer
import cube8540.book.batch.infra.naver.com.NaverBookAPIErrorDeserializer
import cube8540.book.batch.infra.nl.go.NationalLibraryAPIDeserializer
import cube8540.book.batch.infra.nl.go.NationalLibraryAPIErrorDeserializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
class DefaultMapperConfiguration {

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

    @Bean
    fun nationalLibraryObjectMapper(
        @Qualifier("nationalLibraryPublisherRawMapper") publisherRawMapper: PublisherRawMapper
    ): ObjectMapper = ObjectMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(BookAPIResponse::class.java, NationalLibraryAPIDeserializer(publisherRawMapper))
                .addDeserializer(BookAPIErrorResponse::class.java, NationalLibraryAPIErrorDeserializer())
        )
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)

    @Bean
    fun naverBookAPIObjectMapper(
        @Qualifier("naverPublisherRawMapper") publisherRawMapper: PublisherRawMapper
    ): ObjectMapper = ObjectMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(BookAPIResponse::class.java, NaverBookAPIDeserializer(publisherRawMapper))
                .addDeserializer(BookAPIErrorResponse::class.java, NaverBookAPIErrorDeserializer())
        )

    @Bean
    fun kyoboBookDocumentMapper(
        @Qualifier("kyoboDivisionRawMapper") divisionRawMapper: DivisionRawMapper
    ) = KyoboBookDocumentMapper(divisionRawMapper)

}