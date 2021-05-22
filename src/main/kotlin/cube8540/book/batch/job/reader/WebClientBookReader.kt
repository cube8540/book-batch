package cube8540.book.batch.job.reader

import cube8540.book.batch.domain.BookDetailsContext
import cube8540.book.batch.external.BookAPIErrorResponse
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.DefaultPageDecision
import cube8540.book.batch.external.PageDecision
import cube8540.book.batch.external.exception.DefaultErrorCodeExternalExceptionCreator
import cube8540.book.batch.external.exception.ErrorCodeExternalExceptionCreator
import org.springframework.batch.item.database.AbstractPagingItemReader
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.util.retry.Retry
import java.time.Duration

open class WebClientBookReader(
    private val uriBuilder: UriBuilder,
    private val webClient: WebClient
) : AbstractPagingItemReader<BookDetailsContext>() {

    companion object {
        const val defaultRetryCount = 1
        const val defaultRetryDelaySecond = 5
    }

    var pageDecision: PageDecision = DefaultPageDecision()

    lateinit var requestPageParameterName: String
    lateinit var requestPageSizeParameterName: String

    var retryCount: Int = defaultRetryCount
    var retryDelaySecond: Int = defaultRetryDelaySecond

    var exceptionCreator: ErrorCodeExternalExceptionCreator = DefaultErrorCodeExternalExceptionCreator()

    override fun doReadPage() {
        val response = exchange()

        if (response is BookAPIErrorResponse) {
            throw exceptionCreator.create(response.code, response.message)
        }

        if (results == null) {
            results = ArrayList<BookDetailsContext>()
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
                .replaceQueryParam(requestPageParameterName, pageDecision.calculation(page + 1, pageSize))
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
        .retryWhen(Retry.fixedDelay(retryCount.toLong(), Duration.ofSeconds(retryDelaySecond.toLong())))
        .block()
}