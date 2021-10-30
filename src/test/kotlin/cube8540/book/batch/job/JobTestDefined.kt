package cube8540.book.batch.job

import cube8540.book.batch.book.domain.*
import cube8540.book.batch.external.BookUpstreamAPIRequest
import cube8540.book.batch.external.BookUpstreamAPIRequestDetails
import cube8540.book.batch.external.BookUpstreamExternalLink
import java.net.URI
import java.time.LocalDate

val defaultBookUpstreamExternalLink = mapOf(
    MappingType.KYOBO to BookUpstreamExternalLink(defaultLink, defaultOriginalPrice, defaultSalePrice),
    MappingType.ALADIN to BookUpstreamExternalLink(defaultLink, defaultOriginalPrice, defaultSalePrice)
)

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
    upstreamExternalLink: Map<MappingType, BookUpstreamExternalLink>? = defaultBookUpstreamExternalLink
): BookUpstreamAPIRequestDetails =
    BookUpstreamAPIRequestDetails(
        isbn,
        title,
        publishDate,
        publisher,
        seriesIsbn,
        seriesCode,
        largeThumbnail,
        mediumThumbnail,
        smallThumbnail,
        authors,
        description,
        index,
        upstreamExternalLink
    )

fun createBookUpstreamRequest(
    vararg request: BookUpstreamAPIRequestDetails
): BookUpstreamAPIRequest = BookUpstreamAPIRequest(request.toList())