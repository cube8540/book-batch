package cube8540.book.batch.translator.nl.go.application

import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.translator.*
import cube8540.book.batch.translator.client.ErrorCodeExternalExceptionCreator
import cube8540.book.batch.translator.nl.go.client.*
import java.time.format.DateTimeFormatter

class NationalLibraryExchanger(
    private val client: NationalLibraryClient,
    private val publisherRawMapper: PublisherRawMapper,
    private val key: NationalLibraryAPIKey,
    private val exchangeCreator: ErrorCodeExternalExceptionCreator = NationalLibraryAPIErrorCodeExceptionCreator(),
    private val pageDecision: PageDecision = DefaultPageDecision()
): ExternalBookAPIExchanger {

    companion object {
        val DEFAULT_FORMATTER: DateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE
    }

    var formatter: DateTimeFormatter = DEFAULT_FORMATTER

    override fun exchange(request: BookAPIRequest): BookAPIResponse {
        val requestedPage = pageDecision.calculation(request.page!!, request.size!!)
        if (requestedPage > MAX_REQUEST_PAGE) {
            return BookAPIResponse(0, 0, emptyList())
        }

        val bookRequest = NationalLibraryBookRequest(
            secretKey = key.key,
            pageNo = requestedPage, pageSize = request.size,
            startPublishDate = request.from?.format(formatter), endPublishDate = request.to?.format(formatter),
            publisher = request.publisher,
            isbn = request.isbn
        )

        val clientResponse = client.search(bookRequest)
        if (clientResponse.result?.equals(ERROR_RESULT) == true) {
            throw exchangeCreator.create(clientResponse.errorCode!!, clientResponse.errorMessage!!)
        }

        return BookAPIResponse(
            totalCount = clientResponse.totalCount.toLong(), page = clientResponse.pageNo.toLong(),
            books = clientResponse.docs?.map { NationalLibraryBookContext(it, publisherRawMapper) } ?: emptyList()
        )
    }
}