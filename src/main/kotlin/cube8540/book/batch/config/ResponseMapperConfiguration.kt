package cube8540.book.batch.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.naver.com.NaverAPIJsonNodeDeserializer
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializer
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializer
import org.springframework.context.annotation.Configuration

@Configuration
class ResponseMapperConfiguration {

    fun nationalLibraryObjectMapper(publisherRawMapper: PublisherRawMapper) = ObjectMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(BookAPIResponse::class.java, NationalLibraryAPIDeserializer(publisherRawMapper))
        )
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)

    fun naverLibraryObjectMapper(publisherRawMapper: PublisherRawMapper) = XmlMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(JsonNode::class.java, NaverAPIJsonNodeDeserializer())
                .addDeserializer(BookAPIResponse::class.java, NaverBookAPIDeserializer(publisherRawMapper))
        )

}