package cube8540.book.batch.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import cube8540.book.batch.domain.DivisionRawMapper
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.domain.repository.DivisionRepository
import cube8540.book.batch.domain.repository.PublisherRepository
import cube8540.book.batch.external.BookAPIErrorResponse
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.kyobo.kr.KyoboBookDocumentMapper
import cube8540.book.batch.external.naver.com.NaverAPIJsonNodeDeserializer
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializer
import cube8540.book.batch.external.naver.com.NaverBookAPIErrorDeserializer
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializer
import cube8540.book.batch.external.nl.go.NationalLibraryAPIErrorDeserializer
import cube8540.book.batch.infra.DefaultDivisionRawMapper
import cube8540.book.batch.infra.DefaultPublisherRawMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
class ResponseMapperConfiguration {

    @Bean
    fun naverPublisherRawMapper(repository: PublisherRepository) = DefaultPublisherRawMapper(MappingType.NAVER_BOOK, repository)

    @Bean
    fun nationalPublisherRawMapper(repository: PublisherRepository) = DefaultPublisherRawMapper(MappingType.NATIONAL_LIBRARY, repository)

    @Bean
    fun nationalDivisionRawMapper(repository: DivisionRepository) = DefaultDivisionRawMapper(MappingType.NATIONAL_LIBRARY, repository)

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
        @Qualifier("nationalPublisherRawMapper") publisherRawMapper: PublisherRawMapper
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
    ): ObjectMapper = XmlMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(JsonNode::class.java, NaverAPIJsonNodeDeserializer())
                .addDeserializer(BookAPIResponse::class.java, NaverBookAPIDeserializer(publisherRawMapper))
                .addDeserializer(BookAPIErrorResponse::class.java, NaverBookAPIErrorDeserializer())
        )

    @Bean
    fun kyoboBookDocumentMapper(
        @Qualifier("nationalDivisionRawMapper") divisionRawMapper: DivisionRawMapper
    ) = KyoboBookDocumentMapper(divisionRawMapper)

}