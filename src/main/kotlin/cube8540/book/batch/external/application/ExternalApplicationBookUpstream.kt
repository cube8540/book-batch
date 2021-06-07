package cube8540.book.batch.external.application

import cube8540.book.batch.EndpointProperty
import cube8540.book.batch.external.BookUpstreamAPIRequest
import cube8540.book.batch.external.ExternalBookAPIUpstream
import org.springframework.web.reactive.function.client.WebClient

class ExternalApplicationBookUpstream(
    private val webClient: WebClient,
    private val endpointProperty: EndpointProperty
): ExternalBookAPIUpstream {
    override fun upstream(upstreamRequest: BookUpstreamAPIRequest) {
        val result = webClient.post()
            .uri { it.path(endpointProperty.application.upstream).build() }
            .bodyValue(upstreamRequest)
            .exchangeToMono { it.toEntity(String::class.java) }
            .block()
        if (result == null || result.statusCode.isError) {
            throw ExternalUpstreamFailsExceptions("${result?.statusCode?.reasonPhrase} ${result?.body}" )
        }
    }
}