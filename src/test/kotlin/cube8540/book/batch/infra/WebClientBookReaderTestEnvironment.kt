package cube8540.book.batch.infra

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import cube8540.book.batch.domain.BookDetailsContext
import cube8540.book.batch.domain.OriginalPropertyKey
import cube8540.book.batch.external.BookAPIErrorResponse
import cube8540.book.batch.external.BookAPIResponse
import io.mockk.mockk
import okhttp3.mockwebserver.MockResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

object WebClientBookReaderTestEnvironment {

    internal const val endpoint = "/endpoint"
    internal val bookDetailsMockTestKotlinObjectMapper = ObjectMapper()
        .registerModule(KotlinModule())
        .registerModule(
            SimpleModule()
                .addDeserializer(BookAPIResponse::class.java, MockTestDeserializer())
                .addKeyDeserializer(OriginalPropertyKey::class.java, object: KeyDeserializer() {
                    override fun deserializeKey(key: String?, ctxt: DeserializationContext?): Any = "key"
                })
        )

    internal const val errorCode = "errorCode0001"
    internal const val errorMessage = "errorMessage0001"

    internal const val pageRequestName = "PageRequestName"
    internal const val pageSizeRequestName = "PageSizeRequestName"

    internal const val totalCount = 1L
    internal const val pageNumber = 1L
    internal const val isbn = "9791136202093"

    internal val bookDetailsContext: BookDetailsContext = mockk(relaxed = true)

    internal val mockResponse = MockResponse()
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody("{\"data\": 1}")

    internal val mockEmptyResponse = MockResponse()
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody("{\"data\": 0}")

    internal val mockErrorResponse = MockResponse()
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setResponseCode(400)
        .setBody(bookDetailsMockTestKotlinObjectMapper.writeValueAsString(BookAPIErrorResponse(errorCode, errorMessage)))

    internal class MockTestDeserializer: StdDeserializer<BookAPIResponse>(BookAPIResponse::class.java) {
        override fun deserialize(p0: JsonParser?, p1: DeserializationContext?): BookAPIResponse {
            val node = p0?.codec?.readTree<JsonNode>(p0)
            return if (node?.get("data")?.asInt()?.equals(1) == true) {
                BookAPIResponse(totalCount, pageNumber, listOf(bookDetailsContext))
            } else {
                BookAPIResponse(totalCount, pageNumber, emptyList())
            }
        }
    }
}