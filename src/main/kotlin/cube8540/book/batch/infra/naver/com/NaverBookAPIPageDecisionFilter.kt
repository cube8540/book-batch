package cube8540.book.batch.infra.naver.com

import cube8540.book.batch.getQueryParams
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

class NaverBookAPIPageDecisionFilter: ExchangeFilterFunction {
    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
        val queryMap = request.url().getQueryParams()

        val start = queryMap[NaverBookAPIRequestNames.start]!!.first()!!.toInt()
        val display = queryMap[NaverBookAPIRequestNames.display]!!.first()!!.toInt()

        val newStart = when (start == 1) {
            true -> start
            else -> start + display
        }
        val newUrl = UriComponentsBuilder.fromUri(request.url())
            .replaceQueryParam(NaverBookAPIRequestNames.start, newStart)
            .replaceQueryParam(NaverBookAPIRequestNames.display, display)
            .build().toUri()

        return next.exchange(ClientRequest.from(request).url(newUrl).build())
    }
}