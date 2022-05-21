package cube8540.book.batch.external.application

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import cube8540.book.batch.book.domain.*
import cube8540.book.batch.translator.BookUpstreamAPIRequest
import cube8540.book.batch.translator.BookUpstreamAPIRequestDetails
import cube8540.book.batch.translator.BookUpstreamExternalLink
import cube8540.book.batch.external.kyobo.kr.defaultIndexes
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.net.URI
import java.time.LocalDate

val defaultObjectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

val defaultBookUpstreamExternalLink = mapOf(
    MappingType.KYOBO to BookUpstreamExternalLink(defaultLink, defaultOriginalPrice, defaultSalePrice),
    MappingType.ALADIN to BookUpstreamExternalLink(defaultLink, defaultOriginalPrice, defaultSalePrice)
)

fun createUpstreamResponseJson(
    successBooks: List<String> = emptyList(),
    failedBooks: JsonNode?
): JsonNode {
    val objectMapper = ObjectMapper()

    val successArray = objectMapper.createArrayNode()
    successBooks.forEach { successArray.add(it) }

    val node = objectMapper.createObjectNode()
    node.set<JsonNode>(ExternalUpstreamResponse::successBooks.name, successArray)
    failedBooks?.let { node.set<JsonNode>(ExternalUpstreamResponse::failedBooks.name, it) }

    return node
}

fun createUpstreamFailedBooksJson(
    isbn: String = defaultIsbn,
    errors: JsonNode?
): JsonNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createObjectNode()
    node.set<JsonNode>(ExternalUpstreamFailedBooks::isbn.name, objectMapper.convertValue(isbn, JsonNode::class.java))
    errors?.let { node.set<JsonNode>(ExternalUpstreamFailedBooks::errors.name, it) }

    return node
}

fun createUpstreamFailedReasonJson(
    property: String = defaultFailedReasonProperty,
    reason: String = defaultFailedReasonMessage
): JsonNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createObjectNode()
    node.set<JsonNode>(ExternalUpstreamFailedReason::property.name, objectMapper.convertValue(property, JsonNode::class.java))
    node.set<JsonNode>(ExternalUpstreamFailedReason::message.name, objectMapper.convertValue(reason, JsonNode::class.java))

    return node
}

fun jsonArrayNode(vararg values: JsonNode): ArrayNode {
    val objectMapper = ObjectMapper()

    val node = objectMapper.createArrayNode()
    node.addAll(values.asList().toMutableList())

    return node
}

fun createUpstreamBook(
    vararg requests: BookUpstreamAPIRequestDetails
): BookUpstreamAPIRequest = BookUpstreamAPIRequest(requests.toList())

fun createUpstreamBookDetails(
    isbn: String = defaultIsbn,
    title: String = defaultTitle,
    publishDate: LocalDate = defaultPublishDate,
    publisher: String = defaultPublisher,
    seriesIsbn: String? = defaultSeriesIsbn,
    seriesCode: String? = defaultSeriesCode,
    largeThumbnail: URI? = defaultLargeThumbnail,
    mediumThumbnail: URI? = defaultMediumThumbnail,
    smallThumbnail: URI? = defaultSmallThumbnail,
    authors: List<String>? = defaultAuthors.toList(),
    description: String? = defaultDescription,
    indexes: List<String>? = defaultIndexes,
    externalLink: Map<MappingType, BookUpstreamExternalLink>? = defaultBookUpstreamExternalLink,
    confirmedPublication: Boolean? = false
): BookUpstreamAPIRequestDetails =
    BookUpstreamAPIRequestDetails(isbn, title, publishDate, publisher, seriesIsbn, seriesCode, largeThumbnail, mediumThumbnail, smallThumbnail, authors, description, indexes, externalLink, confirmedPublication)

fun createExternalUpstreamDispatcher(
    params: BookUpstreamAPIRequest,
    path: ExternalApplicationEndpointInfo,
    result: MockResponse,
    objectMapper: ObjectMapper = defaultObjectMapper
): Dispatcher {
    return object: Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse =
            if (isEqualParams(request, params, path, objectMapper)) {
                result
            } else {
                MockResponse().setResponseCode(404)
            }
    }
}

fun isEqualParams(
    request: RecordedRequest,
    params: BookUpstreamAPIRequest,
    path: ExternalApplicationEndpointInfo,
    objectMapper: ObjectMapper
): Boolean {
    val requestedBody = request.body.readUtf8()
    val expectedBody = objectMapper.writeValueAsString(params)

    return request.requestUrl!!.toUri().path == path.upstream && requestedBody == expectedBody
}