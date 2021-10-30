package cube8540.book.batch.external

import cube8540.book.batch.book.domain.BookDetailsContext
import java.net.URI
import java.time.LocalDate

interface ExternalBookAPIExchanger {
    fun exchange(request: BookAPIRequest): BookAPIResponse?
}

interface ExternalBookAPIUpstream {
    fun upstream(upstreamRequest: BookUpstreamAPIRequest)
}

data class BookAPIRequest(
    val page: Int? = null,
    val size: Int? = null,
    val from: LocalDate? = null,
    val to: LocalDate? = null,
    val isbn: String? = null,
    val publisher: String? = null
)

data class BookUpstreamAPIRequest(val requests: List<BookUpstreamAPIRequestDetails>)
data class BookUpstreamAPIRequestDetails(
    val isbn: String,
    val title: String,
    val publishDate: LocalDate,
    val publisherCode: String,
    val seriesIsbn: String? = null,
    val seriesCode: String? = null,
    val largeThumbnail: URI? = null,
    val mediumThumbnail: URI? = null,
    val smallThumbnail: URI? = null,
    val authors: List<String>? = null,
    val description: String? = null,
    val indexes: List<String>? = null
)

data class BookAPIResponse(
    val totalCount: Long = 0,
    val page: Long = 0,
    val books: List<BookDetailsContext> = emptyList()
)

data class BookAPIErrorResponse(
    val code: String,
    val message: String
)