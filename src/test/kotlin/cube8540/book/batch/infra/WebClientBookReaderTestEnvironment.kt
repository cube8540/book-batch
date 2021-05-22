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
import okhttp3.mockwebserver.MockWebServer
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

object WebClientBookReaderTestEnvironment {

    private const val successfulResponse = "{\"success\": \"true\"}"
    private const val emptyResponse = "{\"empty\": \"true\"}"
    private const val error = "{\"error\": \"true\"}"

    internal const val endpoint = "/endpoint"

    internal const val errorCode = "errorCode0001"
    internal const val errorMessage = "errorMessage0001"

    internal const val pageRequestName = "PageRequestName"
    internal const val pageSizeRequestName = "PageSizeRequestName"

    internal const val totalCount = 1L
    internal const val pageNumber = 1L
    internal const val isbn = "9791136202093"

    internal val bookDetailsContext: BookDetailsContext = mockk(relaxed = true)

    internal val mockSuccessfulResponse = MockResponse()
        .setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody(successfulResponse)

    internal val mockEmptyResponse = MockResponse()
        .setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody(emptyResponse)

    internal val mockErrorResponse = MockResponse()
        .setResponseCode(400)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody(error)

    internal fun createWebClient(mockWebServer: MockWebServer): WebClient {
        val httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(1))
        val kotlinObjectMapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(
                SimpleModule()
                    .addKeyDeserializer(OriginalPropertyKey::class.java, object: KeyDeserializer() {
                        override fun deserializeKey(key: String?, ctxt: DeserializationContext?): Any = "key"
                    })
                    .addDeserializer(BookAPIResponse::class.java, MockTestDeserializer())
                    .addDeserializer(BookAPIErrorResponse::class.java, MockTestErrorDeserializer())
            )
        return WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(
                ExchangeStrategies.builder().codecs {
                    it.customCodecs().register(Jackson2JsonEncoder(kotlinObjectMapper))
                    it.customCodecs().register(Jackson2JsonDecoder(kotlinObjectMapper))
                }
                .build()
            )
            .build()
    }

    internal class MockTestDeserializer: StdDeserializer<BookAPIResponse>(BookAPIResponse::class.java) {
        override fun deserialize(p0: JsonParser?, p1: DeserializationContext?): BookAPIResponse {
            val node = p0?.codec?.readTree<JsonNode>(p0)
            return when {
                node?.get("success")?.asBoolean() == true -> {
                    BookAPIResponse(totalCount, pageNumber, listOf(bookDetailsContext))
                }
                else -> {
                    BookAPIResponse(totalCount, pageNumber, emptyList())
                }
            }
        }
    }

    internal class MockTestErrorDeserializer: StdDeserializer<BookAPIErrorResponse>(BookAPIErrorResponse::class.java) {
        override fun deserialize(p0: JsonParser?, p1: DeserializationContext?): BookAPIErrorResponse =
            BookAPIErrorResponse(errorCode, errorMessage)
    }
}