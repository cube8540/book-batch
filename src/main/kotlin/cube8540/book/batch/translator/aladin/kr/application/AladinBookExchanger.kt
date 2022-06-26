package cube8540.book.batch.translator.aladin.kr.application

import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.translator.*
import cube8540.book.batch.translator.aladin.kr.client.*

class AladinBookExchanger(
    private val client: AladinBookClient,
    private val key: AladinAuthenticationInfo,
    private val publisherRawMapper: PublisherRawMapper,
    private val pageDecision: PageDecision = DefaultPageDecision()
): ExternalBookAPIExchanger {

    override fun exchange(request: BookAPIRequest): BookAPIResponse {
        if (request.page!! * request.size!! > MAX_REQUEST_DATA_COUNT) {
            return BookAPIResponse(0, 0, emptyList())
        }

        if (request.size > MAX_RESULTS) {
            return BookAPIResponse(0, 0, emptyList())
        }

        val clientRequest = AladinBookRequest(ttbKey = key.ttbKey, start = pageDecision.calculation(request.page, request.size), maxResults = request.size, query = request.publisher!!)
        val clientResponse = client.search(clientRequest)

        val requestedBooks = clientResponse.items
            ?.filter { it.publishDate?.let { d -> d <= request.to && d >= request.from } ?: false } ?: emptyList()

        return BookAPIResponse(
            totalCount = clientResponse.totalResults.toLong(),
            page = clientResponse.startIndex.toLong(),
            books = requestedBooks.map { AladinBookResponseContext(it, publisherRawMapper) }
        )
    }
}