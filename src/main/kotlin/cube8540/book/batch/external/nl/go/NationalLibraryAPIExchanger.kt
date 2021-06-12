package cube8540.book.batch.external.nl.go

import cube8540.book.batch.external.*
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.util.retry.Retry
import java.net.URI
import java.time.Duration
import java.time.format.DateTimeFormatter

class NationalLibraryAPIExchanger(private val webClient: WebClient, private val key: NationalLibraryAPIKey): ExternalBookAPIExchanger {

    companion object {
        const val defaultRetryCount = 1
        const val defaultRetryDelaySecond = 5
    }

    var pageDecision: PageDecision = DefaultPageDecision()

    var retryCount: Int = defaultRetryCount
    var retryDelaySecond: Int = defaultRetryDelaySecond

    override fun exchange(request: BookAPIRequest): BookAPIResponse? {
        val uriBuilder = UriComponentsBuilder.newInstance()
            .uri(URI.create(NationalLibraryAPIRequestNames.endpointPath))
            .queryParam(NationalLibraryAPIRequestNames.secretKey, key.key)
            .queryParam(NationalLibraryAPIRequestNames.fromKeyword, request.from?.format(DateTimeFormatter.BASIC_ISO_DATE))
            .queryParam(NationalLibraryAPIRequestNames.toKeyword, request.to?.format(DateTimeFormatter.BASIC_ISO_DATE))
            .queryParam(NationalLibraryAPIRequestNames.isbnKeyword, request.isbn)
            .queryParam(NationalLibraryAPIRequestNames.resultStyle, "json")
            .queryParam(NationalLibraryAPIRequestNames.ebookYN, "N")
            .queryParam(NationalLibraryAPIRequestNames.pageSize, request.size)
            .queryParam(NationalLibraryAPIRequestNames.pageNumber, pageDecision.calculation(request.page!!, request.size!!))
            .queryParam(NationalLibraryAPIRequestNames.publisherKeyword, request.publisher)
            .build()

        return webClient.get()
            .uri(uriBuilder.toUriString())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(BookAPIResponse::class.java)
            .retryWhen(Retry.fixedDelay(retryCount.toLong(), Duration.ofSeconds(retryDelaySecond.toLong())))
            .block()
    }
}