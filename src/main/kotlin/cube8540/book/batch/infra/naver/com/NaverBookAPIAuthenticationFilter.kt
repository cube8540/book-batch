package cube8540.book.batch.infra.naver.com

import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono

class NaverBookAPIAuthenticationFilter(private val clientId: String, private val clientSecret: String): ExchangeFilterFunction {

    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
        val newClientRequest = ClientRequest.from(request)
            .header(NaverBookAPIRequestNames.clientId, clientId)
            .header(NaverBookAPIRequestNames.clientSecret, clientSecret)
            .build()

        return next.exchange(newClientRequest)
    }
}