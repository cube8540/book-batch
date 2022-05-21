package cube8540.book.batch.external.nl.go

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import cube8540.book.batch.book.domain.*
import cube8540.book.batch.interlock.BookAPIErrorResponse
import cube8540.book.batch.interlock.BookAPIRequest
import cube8540.book.batch.interlock.BookAPIResponse
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

const val defaultAdditionalCode = "07650"

val defaultRealPublishDate: LocalDate = LocalDate.of(2021, 6, 6)

const val defaultExpression = ""

const val defaultErrorMessage = "인증키 정보가 없습니다."
const val defaultErrorCode = "010"
const val defaultErrorResult = "ERROR"

val defaultNationalLibraryClientKey = UUID.randomUUID().toString()

fun createNationalLibraryAPIResponse(
    total: Int? = 1,
    page: Int? = 1,
    docs: JsonNode?
): JsonNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createObjectNode()
    total?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.totalCount, objectMapper.convertValue(it, JsonNode::class.java)) }
    page?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.pageNo, objectMapper.convertValue(it, JsonNode::class.java)) }
    docs?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.documents, docs) }

    return node
}

fun createNationalLibraryAPIErrorResponse(
    errorCode: String? = defaultErrorCode,
    errorMessage: String? = defaultErrorMessage,
    errorResult: String? = defaultErrorResult
): JsonNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createObjectNode()
    errorCode?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.errorCode, objectMapper.convertValue(it, JsonNode::class.java)) }
    errorMessage?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.errorMessage, objectMapper.convertValue(it, JsonNode::class.java)) }
    errorResult?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.result, objectMapper.convertValue(it, JsonNode::class.java)) }

    return node
}

fun createBookJsonArrayNode(vararg values: JsonNode): ArrayNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createArrayNode()
    node.addAll(values.asList().toMutableList())

    return node
}

fun createBookJsonNode(
    isbn: String? = defaultIsbn,
    additionalCode: String? = defaultAdditionalCode,
    seriesIsbn: String? = defaultSeriesIsbn,
    setExpression: String? = defaultExpression,
    publisher: String? = defaultPublisherCode,
    title: String? = defaultTitle,
    publishDate: String? = defaultPublishDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
    realPublishDate: String? = defaultRealPublishDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
    seriesNo: Int? = null
): JsonNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createObjectNode()
    isbn?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.isbn, objectMapper.convertValue(it, JsonNode::class.java)) }
    additionalCode?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.additionalCode, objectMapper.convertValue(it, JsonNode::class.java)) }
    seriesIsbn?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.setIsbn, objectMapper.convertValue(it, JsonNode::class.java)) }
    setExpression?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.setExpression, objectMapper.convertValue(it, JsonNode::class.java)) }
    publisher?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.publisher, objectMapper.convertValue(it, JsonNode::class.java)) }
    title?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.title, objectMapper.convertValue(it, JsonNode::class.java)) }
    seriesNo?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.seriesNo, objectMapper.convertValue(it, JsonNode::class.java)) }
    realPublishDate?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.realPublishDate,objectMapper.convertValue(it, JsonNode::class.java)) }
    publishDate?.let { node.set<JsonNode>(NationalLibraryAPIResponseNames.publishPreDate,objectMapper.convertValue(it, JsonNode::class.java)) }

    return node
}

fun createNationalLibraryWebClient(mockWebServer: MockWebServer, publisherRawMapper: PublisherRawMapper): WebClient {
    val objectMapper = ObjectMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(BookAPIResponse::class.java, NationalLibraryAPIDeserializer(publisherRawMapper))
                .addDeserializer(BookAPIErrorResponse::class.java, NationalLibraryAPIErrorDeserializer())
        )
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
    val httpClient = HttpClient.create()
        .responseTimeout(Duration.ofSeconds(1.toLong()))
    return WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .exchangeStrategies(
            ExchangeStrategies.builder().codecs {
                it.customCodecs().register(Jackson2JsonDecoder(objectMapper))
                it.customCodecs().register(Jackson2JsonEncoder(objectMapper))
            }.build()
        ).build()
}

fun createNationalLibraryDispatcher(
    apiKey: String? = defaultNationalLibraryClientKey,
    params: BookAPIRequest,
    page: Int,
    result: MockResponse
): Dispatcher {
    return object: Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse =
            if (isEqualParams(request, params, apiKey, page)) {
                result
            } else {
                MockResponse().setResponseCode(404)
            }
    }
}

fun isEqualParams(
    request: RecordedRequest,
    params: BookAPIRequest,
    apiKey: String?,
    realRequestedPage: Int
): Boolean {
    val requested = URI.create(request.path!!).getQueryParams()
    val expected = mapOf(
        NationalLibraryAPIRequestNames.secretKey to listOf(apiKey),
        NationalLibraryAPIRequestNames.pageSize to listOf(params.size.toString()),
        NationalLibraryAPIRequestNames.pageNumber to listOf(realRequestedPage.toString()),
        NationalLibraryAPIRequestNames.ebookYN to listOf("N"),
        NationalLibraryAPIRequestNames.resultStyle to listOf("json"),
        NationalLibraryAPIRequestNames.fromKeyword to listOf(params.from!!.format(DateTimeFormatter.BASIC_ISO_DATE)),
        NationalLibraryAPIRequestNames.toKeyword to listOf(params.to!!.format(DateTimeFormatter.BASIC_ISO_DATE)),
        NationalLibraryAPIRequestNames.isbnKeyword to listOf(params.isbn),
        NationalLibraryAPIRequestNames.publisherKeyword to listOf(params.publisher)
    )
    return request.requestUrl!!.toUri().path == NationalLibraryAPIRequestNames.endpointPath && requested == expected
}