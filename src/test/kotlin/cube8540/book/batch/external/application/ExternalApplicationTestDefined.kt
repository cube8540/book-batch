package cube8540.book.batch.external.application

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.book.domain.*
import cube8540.book.batch.external.BookUpstreamAPIRequest
import cube8540.book.batch.external.BookUpstreamAPIRequestDetails
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.net.URI
import java.time.LocalDate

val defaultObjectMapper = ObjectMapper()

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
    price: Double? = defaultPrice
): BookUpstreamAPIRequestDetails =
    BookUpstreamAPIRequestDetails(isbn, title, publishDate, publisher, seriesIsbn, seriesCode, largeThumbnail, mediumThumbnail, smallThumbnail, authors, description, price)

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