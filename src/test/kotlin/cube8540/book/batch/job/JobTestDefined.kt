package cube8540.book.batch.job

import cube8540.book.batch.book.domain.*
import cube8540.book.batch.external.BookUpstreamAPIRequest
import cube8540.book.batch.external.BookUpstreamAPIRequestDetails
import java.net.URI
import java.time.LocalDate

fun createBookUpstreamRequestDetails(
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
    index: List<String>? = defaultBookIndex,
    price: Double? = defaultPrice
): BookUpstreamAPIRequestDetails =
    BookUpstreamAPIRequestDetails(isbn, title, publishDate, publisher, seriesIsbn, seriesCode, largeThumbnail, mediumThumbnail, smallThumbnail, authors, description, index, price)

fun createBookUpstreamRequest(
    vararg request: BookUpstreamAPIRequestDetails
): BookUpstreamAPIRequest = BookUpstreamAPIRequest(request.toList())