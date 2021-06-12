package cube8540.book.batch.external.naver.com

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import cube8540.book.batch.book.domain.*
import cube8540.book.batch.external.BookAPIErrorResponse
import cube8540.book.batch.external.BookAPIRequest
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.getQueryParams
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.net.URI
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

const val defaultNaverBookAPIResponseIsbn = "1136215301 $defaultIsbn"

const val defaultErrorMessage = "인증키 정보가 없습니다."
const val defaultErrorCode = "010"

val defaultClientId = UUID.randomUUID().toString()
val defaultClientSecret = UUID.randomUUID().toString()

fun createNaverBookAPIResponse(
    total: Int? = 1,
    start: Int? = 1,
    display: Int? = 1,
    items: JsonNode?
): JsonNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createObjectNode()
    total?.let { node.set<JsonNode>(NaverBookAPIResponseNames.totalCount, objectMapper.convertValue(it, JsonNode::class.java)) }
    start?.let { node.set<JsonNode>(NaverBookAPIResponseNames.start, objectMapper.convertValue(it, JsonNode::class.java)) }
    display?.let { node.set<JsonNode>(NaverBookAPIResponseNames.display, objectMapper.convertValue(it, JsonNode::class.java)) }
    items?.let { node.set<JsonNode>(NaverBookAPIResponseNames.item, items) }

    return node
}

fun createNaverBookErrorResponse(
    errorCode: String? = defaultErrorCode,
    errorMessage: String? = defaultErrorMessage
): JsonNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createObjectNode()
    errorCode?.let { node.set<JsonNode>(NaverBookAPIResponseNames.errorCode, objectMapper.convertValue(it, JsonNode::class.java)) }
    errorMessage?.let { node.set<JsonNode>(NaverBookAPIResponseNames.errorMessage, objectMapper.convertValue(it, JsonNode::class.java)) }

    return node
}

fun createBookJsonArrayNode(vararg values: JsonNode): ArrayNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createArrayNode()
    node.addAll(values.asList().toMutableList())

    return node
}

fun createBookJsonNode(
    isbn: String? = defaultNaverBookAPIResponseIsbn,
    title: String? = defaultTitle,
    image: String? = defaultSmallThumbnail.toString(),
    publisher: String? = defaultPublisherCode,
    publishDate: LocalDate? = defaultPublishDate
): JsonNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createObjectNode()
    isbn?.let { node.set<JsonNode>(NaverBookAPIResponseNames.isbn,
        objectMapper.convertValue(it, JsonNode::class.java)) }
    title?.let { node.set<JsonNode>(NaverBookAPIResponseNames.title,
        objectMapper.convertValue(it, JsonNode::class.java)) }
    image?.let { node.set<JsonNode>(NaverBookAPIResponseNames.image,
        objectMapper.convertValue(it, JsonNode::class.java)) }
    publisher?.let { node.set<JsonNode>(NaverBookAPIResponseNames.publisher,
        objectMapper.convertValue(it, JsonNode::class.java)) }
    publishDate?.let { node.set<JsonNode>(NaverBookAPIResponseNames.publishDate,
        objectMapper.convertValue(it.format(DateTimeFormatter.ofPattern("yyyyMMdd")), JsonNode::class.java)) }

    return node
}

fun createNaverBookAPIDispatcher(
    clientId: String? = defaultClientId,
    clientSecret: String? = defaultClientSecret,
    params: BookAPIRequest,
    realRequestedPage: Int,
    result: MockResponse
): Dispatcher {
    return object: Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse =
            if (isEqualParams(request, params, clientId, clientSecret, realRequestedPage)) {
                result
            } else {
                MockResponse().setResponseCode(404)
            }
    }
}

fun createNaverWebClient(mockWebServer: MockWebServer, publisherMapper: PublisherRawMapper): WebClient {
    val objectMapper = ObjectMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(BookAPIResponse::class.java, NaverBookAPIDeserializer(publisherMapper))
                .addDeserializer(BookAPIErrorResponse::class.java, NaverBookAPIErrorDeserializer())
        )
    val httpClient = HttpClient.create()
        .responseTimeout(Duration.ofSeconds(1.toLong()))
    return WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .exchangeStrategies(
            ExchangeStrategies.builder().codecs {
                it.customCodecs().register(Jackson2JsonEncoder(objectMapper))
                it.customCodecs().register(Jackson2JsonDecoder(objectMapper))
            }.build()
        )
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .build()
}

private fun isEqualParams(
    request: RecordedRequest,
    params: BookAPIRequest,
    clientId: String?,
    clientSecret: String?,
    realRequestedPage: Int
): Boolean {
    val requested = URI.create(request.path!!).getQueryParams()
    val expected = mapOf(
        NaverBookAPIRequestNames.fromKeyword to listOf(params.from!!.format(DateTimeFormatter.BASIC_ISO_DATE)),
        NaverBookAPIRequestNames.toKeyword to listOf(params.to!!.format(DateTimeFormatter.BASIC_ISO_DATE)),
        NaverBookAPIRequestNames.display to listOf(params.size.toString()),
        NaverBookAPIRequestNames.start to listOf(realRequestedPage.toString()),
        NaverBookAPIRequestNames.publisherKeyword to listOf(params.publisher),
        NaverBookAPIRequestNames.isbnKeyword to listOf(params.isbn)
    )
    return request.requestUrl!!.toUri().path == NaverBookAPIRequestNames.endpointPath &&
            request.headers[NaverBookAPIRequestNames.clientId] == clientId &&
            request.headers[NaverBookAPIRequestNames.clientSecret] == clientSecret &&
            requested == expected
}