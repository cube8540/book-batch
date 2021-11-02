package cube8540.book.batch.external.aladin.kr

import cube8540.book.batch.external.*
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.util.retry.Retry
import java.net.URI
import java.time.Duration

class AladinAPIExchanger(private val webClient: WebClient, private val key: AladinAuthenticationInfo): ExternalBookAPIExchanger {

    companion object {
        const val defaultRetryCount = 1
        const val defaultRetryDelaySecond = 5

        const val maximumDataCount = 200

        const val defaultQueryType = "Publisher"
        const val defaultSearchTarget = "Book"
        const val defaultOutput = "js"
        const val defaultVersion = "20131101"
        const val defaultSort = "PublishTime"
        const val defaultMaxResults = 50
    }

    var pageDecision: PageDecision = DefaultPageDecision()

    var retryCount: Int = defaultRetryCount
    var retryDelaySecond: Int = defaultRetryDelaySecond

    var queryType = defaultQueryType
    var searchTarget = defaultSearchTarget
    var output = defaultOutput
    var version = defaultVersion
    var sort = defaultSort
    var maxResults = defaultMaxResults

    override fun exchange(request: BookAPIRequest): BookAPIResponse? {
        if (request.page!! * request.size!! > maximumDataCount) {
            return BookAPIResponse(0, 0, emptyList())
        }
        val uriBuilder = UriComponentsBuilder.newInstance()
            .uri(URI.create(AladinAPIRequestNames.endpointPath))
            .queryParam(AladinAPIRequestNames.ttbKey, key.ttbKey)
            .queryParam(AladinAPIRequestNames.queryType, queryType)
            .queryParam(AladinAPIRequestNames.query, request.publisher)
            .queryParam(AladinAPIRequestNames.start, pageDecision.calculation(request.page, request.size))
            .queryParam(AladinAPIRequestNames.maxResults, maxResults)
            .queryParam(AladinAPIRequestNames.searchTarget, searchTarget)
            .queryParam(AladinAPIRequestNames.output, output)
            .queryParam(AladinAPIRequestNames.version, version)
            .queryParam(AladinAPIRequestNames.sort, sort)
            .build()

        val result = webClient.get()
            .uri(uriBuilder.toUriString())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(BookAPIResponse::class.java)
            .retryWhen(Retry.fixedDelay(retryCount.toLong(), Duration.ofSeconds(retryDelaySecond.toLong())))
            .block()

        val booksBetweenThemRequestDate = result?.books
            ?.filter { it.resolvePublishDate()?.let { d -> d <= request.to && d >= request.from } ?: false }

        return booksBetweenThemRequestDate?.let { BookAPIResponse(result.totalCount, result.page, it) }
    }
}