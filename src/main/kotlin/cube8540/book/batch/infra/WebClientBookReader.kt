package cube8540.book.batch.infra

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.external.BookAPIErrorResponse
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.exception.DefaultErrorCodeExternalExceptionCreator
import cube8540.book.batch.external.exception.ErrorCodeExternalExceptionCreator
import org.springframework.batch.item.database.AbstractPagingItemReader
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder

open class WebClientBookReader(private val uriBuilder: UriBuilder, private val webClient: WebClient)
    : AbstractPagingItemReader<BookDetails>() {

    lateinit var requestPageParameterName: String
    lateinit var requestPageSizeParameterName: String

    var exceptionCreator: ErrorCodeExternalExceptionCreator = DefaultErrorCodeExternalExceptionCreator()

    override fun doReadPage() {
        val response = exchange()

        if (response is BookAPIErrorResponse) {
            throw exceptionCreator.create(response.code, response.message)
        }

        if (results == null) {
            results = ArrayList<BookDetails>()
        } else {
            results.clear()
        }
        if (response != null) {
            results.addAll((response as BookAPIResponse).books)
        }
    }

    override fun doJumpToPage(itemIndex: Int) {
    }

    private fun exchange() = webClient.get()
        .uri(
            uriBuilder
                .replaceQueryParam(requestPageParameterName, page + 1)
                .replaceQueryParam(requestPageSizeParameterName, pageSize)
                .build()
        )
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToMono {
            if (it.statusCode().isError) {
                it.bodyToMono(BookAPIErrorResponse::class.java)
            } else {
                it.bodyToMono(BookAPIResponse::class.java)
            }
        }
        .block()
}