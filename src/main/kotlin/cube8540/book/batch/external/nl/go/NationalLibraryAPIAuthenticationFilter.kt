package cube8540.book.batch.external.nl.go

import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

class NationalLibraryAPIAuthenticationFilter(private val secretKey: String): ExchangeFilterFunction {

    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
        val newUrl = UriComponentsBuilder.fromUri(request.url())
            .queryParam(NationalLibraryAPIRequestNames.secretKey, secretKey)
            .build()
            .toUri()
        return next.exchange(ClientRequest.from(request).url(newUrl).build())
    }
}