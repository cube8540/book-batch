package cube8540.book.batch.translator.naver.com.application

import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.translator.BookAPIRequest
import cube8540.book.batch.translator.BookAPIResponse
import cube8540.book.batch.translator.ExternalBookAPIExchanger
import cube8540.book.batch.translator.PageDecision
import cube8540.book.batch.translator.naver.com.client.NaverBookClient
import cube8540.book.batch.translator.naver.com.client.NaverBookClientRequest
import java.time.format.DateTimeFormatter

class NaverBookExchanger(
    private val client: NaverBookClient,
    private val pageDecision: PageDecision,
    private val publisherRawMapper: PublisherRawMapper
): ExternalBookAPIExchanger {

    companion object {
        const val DEFAULT_MAXIMUM_REQUEST_PAGE = 999
    }

    var maxRequestPage = DEFAULT_MAXIMUM_REQUEST_PAGE

    override fun exchange(request: BookAPIRequest): BookAPIResponse {
        val requestPage = pageDecision.calculation(request.page!!, request.size!!)
        if (requestPage >= maxRequestPage) {
            return BookAPIResponse(totalCount = 0, page = 0, books = emptyList())
        }

        val dateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE
        val clientRequest = NaverBookClientRequest(
            start = requestPage, display = request.size, publisher = request.publisher, isbn = request.isbn,
            from = request.from?.format(dateTimeFormatter), to = request.to?.format(dateTimeFormatter)
        )
        val clientResponse = client.search(clientRequest)
        return BookAPIResponse(
            totalCount = clientResponse.total.toLong(), page = clientResponse.start.toLong(),
            books = clientResponse.items?.map { NaverBookResponseContext(it, publisherRawMapper) } ?: emptyList()
        )
    }
}