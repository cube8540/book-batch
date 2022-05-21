package cube8540.book.batch.external.aladin.kr

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import cube8540.book.batch.book.domain.*
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
import kotlin.random.Random

const val defaultTtbKey = "ttbKey000000"

val defaultCategoryId = Random.nextInt(0, Int.MAX_VALUE)

fun createAladinBookAPIResponse(
    total: Int? = 1,
    start: Int? = 1,
    items: JsonNode?
): JsonNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createObjectNode()
    total?.let { node.set<JsonNode>(AladinAPIResponseNames.totalCount, objectMapper.convertValue(it, JsonNode::class.java)) }
    start?.let { node.set<JsonNode>(AladinAPIResponseNames.page, objectMapper.convertValue(it, JsonNode::class.java)) }
    items?.let { node.set<JsonNode>(AladinAPIResponseNames.items, it) }

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
    title: String? = defaultTitle,
    author: Set<String>? = defaultAuthors,
    publisher: String? = defaultPublisherCode,
    publishDate: LocalDate? = defaultPublishDate,
    categoryId: Int? = defaultCategoryId,
    link: String? = defaultLink,
    originalPrice: Double? = defaultOriginalPrice,
    salePrice: Double? = defaultSalePrice
): JsonNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createObjectNode()
    isbn?.let { node.set<JsonNode>(AladinAPIResponseNames.isbn, objectMapper.convertValue(it, JsonNode::class.java)) }
    title?.let { node.set<JsonNode>(AladinAPIResponseNames.title, objectMapper.convertValue(it, JsonNode::class.java)) }
    author?.let { node.set<JsonNode>(AladinAPIResponseNames.author, objectMapper.convertValue(it.joinToString(separator = ", "), JsonNode::class.java)) }
    publisher?.let { node.set<JsonNode>(AladinAPIResponseNames.publisher, objectMapper.convertValue(it, JsonNode::class.java)) }
    publishDate?.let { node.set<JsonNode>(AladinAPIResponseNames.publishDate, objectMapper.convertValue(it.format(DateTimeFormatter.ISO_DATE), JsonNode::class.java)) }
    categoryId?.let { node.set<JsonNode>(AladinAPIResponseNames.categoryId, objectMapper.convertValue(it, JsonNode::class.java)) }
    link?.let { node.set<JsonNode>(AladinAPIResponseNames.link, objectMapper.convertValue(it, JsonNode::class.java)) }
    originalPrice?.let { node.set<JsonNode>(AladinAPIResponseNames.originalPrice, objectMapper.convertValue(it, JsonNode::class.java)) }
    salePrice?.let { node.set<JsonNode>(AladinAPIResponseNames.salePrice, objectMapper.convertValue(it, JsonNode::class.java)) }

    return node
}

fun createAladinAPIDispatcher(
    ttbKey: String = defaultTtbKey,
    params: BookAPIRequest,
    result: MockResponse,
    realPageRequest: Int
): Dispatcher {
    return object: Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse =
            if (isEqualParams(request, params, ttbKey, realPageRequest)) {
                result
            } else {
                MockResponse().setResponseCode(404)
            }
    }
}

fun createAladinWebClient(mockWebServer: MockWebServer, publisherMapper: PublisherRawMapper): WebClient {
    val objectMapper = ObjectMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(BookAPIResponse::class.java, AladinAPIDeserializer(publisherMapper))
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
    ttbKey: String,
    realPageRequest: Int
): Boolean {
    val requested = URI.create(request.path!!).getQueryParams()
    val expected = mapOf(
        AladinAPIRequestNames.ttbKey to listOf(ttbKey),
        AladinAPIRequestNames.queryType to listOf(AladinAPIExchanger.defaultQueryType),
        AladinAPIRequestNames.query to listOf(params.publisher),
        AladinAPIRequestNames.start to listOf(realPageRequest.toString()),
        AladinAPIRequestNames.maxResults to listOf(AladinAPIExchanger.defaultMaxResults.toString()),
        AladinAPIRequestNames.searchTarget to listOf(AladinAPIExchanger.defaultSearchTarget),
        AladinAPIRequestNames.output to listOf(AladinAPIExchanger.defaultOutput),
        AladinAPIRequestNames.version to listOf(AladinAPIExchanger.defaultVersion),
        AladinAPIRequestNames.sort to listOf(AladinAPIExchanger.defaultSort)
    )

    return request.requestUrl!!.toUri().path == AladinAPIRequestNames.endpointPath &&
            requested == expected
}