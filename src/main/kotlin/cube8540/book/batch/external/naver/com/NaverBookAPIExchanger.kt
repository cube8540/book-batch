package cube8540.book.batch.external.naver.com

import cube8540.book.batch.external.*
import cube8540.book.batch.external.exception.ErrorCodeExternalExceptionCreator
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.util.retry.Retry
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.format.DateTimeFormatter

class NaverBookAPIExchanger(private val webClient: WebClient, private val key: NaverBookAPIKey): ExternalBookAPIExchanger {

    companion object {
        const val defaultRetryCount = 1
        const val defaultRetryDelaySecond = 5
    }

    var pageDecision: PageDecision = NaverBookAPIPageDecision()
    var exceptionCreator: ErrorCodeExternalExceptionCreator = NaverBookAPIErrorCodeExceptionCreator()

    var retryCount: Int = defaultRetryCount
    var retryDelaySecond: Int = defaultRetryDelaySecond

    override fun exchange(request: BookAPIRequest): BookAPIResponse {
        val requestPage = pageDecision.calculation(request.page!!, request.size!!)
        if (requestPage >= 1000) {
            return BookAPIResponse(0, 0, emptyList())
        }

        val uriBuilder = UriComponentsBuilder.newInstance()
            .uri(URI.create(NaverBookAPIRequestNames.endpointPath))
            .queryParam(NaverBookAPIRequestNames.start, pageDecision.calculation(request.page, request.size))
            .queryParam(NaverBookAPIRequestNames.fromKeyword, request.from?.format(DateTimeFormatter.BASIC_ISO_DATE))
            .queryParam(NaverBookAPIRequestNames.toKeyword, request.to?.format(DateTimeFormatter.BASIC_ISO_DATE))
            .queryParam(NaverBookAPIRequestNames.display, request.size)
            .queryParam(NaverBookAPIRequestNames.publisherKeyword, request.publisher)
            .queryParam(NaverBookAPIRequestNames.isbnKeyword, request.isbn)
            .encode(StandardCharsets.UTF_8)

        val result = webClient.get()
            .uri(uriBuilder.toUriString())
            .header(NaverBookAPIRequestNames.clientId, key.clientId)
            .header(NaverBookAPIRequestNames.clientSecret, key.clientSecret)
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

        if (result is BookAPIErrorResponse) {
            throw exceptionCreator.create(result.code, result.message)
        }

        return (result as BookAPIResponse)
    }
}