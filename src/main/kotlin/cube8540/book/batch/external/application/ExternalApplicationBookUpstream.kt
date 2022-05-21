package cube8540.book.batch.external.application

import cube8540.book.batch.EndpointProperty
import cube8540.book.batch.interlock.BookUpstreamAPIRequest
import cube8540.book.batch.interlock.ExternalBookAPIUpstream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class ExternalApplicationBookUpstream
@Autowired
constructor(
    @Qualifier("oauth2ClientRegistrationWebClient")
    private val webClient: WebClient,

    private val eventPublisher: ApplicationEventPublisher,

    private val endpointProperty: EndpointProperty
): ExternalBookAPIUpstream {
    override fun upstream(upstreamRequest: BookUpstreamAPIRequest) {
        val result = webClient.post()
            .uri { it.path(endpointProperty.application.upstream).build() }
            .bodyValue(upstreamRequest)
            .exchangeToMono { it.toEntity(ExternalUpstreamResponse::class.java) }
            .block()
        if (result == null || result.statusCode.isError) {
            throw ExternalUpstreamFailsExceptions("${result?.statusCode?.reasonPhrase} ${result?.body}" )
        } else {
            result.body?.let { eventPublisher.publishEvent(ExternalUpstreamCompletedEvent(it)) }
        }
    }
}