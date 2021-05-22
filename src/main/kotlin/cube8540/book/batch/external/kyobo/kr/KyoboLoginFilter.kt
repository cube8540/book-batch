package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.BatchApplication
import org.springframework.http.ResponseCookie
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.LocalDateTime

class KyoboLoginFilter(private val username: String, private val password: String, private val webClient: WebClient)
    : ExchangeFilterFunction {

    internal var loginInfo: KyoboLoginInfo? = null

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())

        internal const val expiredSeconds = 600
    }

    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
        if (loginInfo == null || loginInfo!!.issuedDateTime.plusSeconds(expiredSeconds.toLong()).isBefore(LocalDateTime.now(clock))) {
            val loginCookies = loginCookies()
            val storedCookies = HashMap<String, String?>()

            loginCookies?.forEach { storedCookies[it.key] = it.value.first().value }
            loginInfo = KyoboLoginInfo(storedCookies, LocalDateTime.now(clock))
        }

        val newClientRequestBuilder = ClientRequest.from(request)
        loginInfo?.cookies?.forEach { newClientRequestBuilder.cookie(it.key, it.value) }

        return next.exchange(newClientRequestBuilder.build())
    }

    private fun loginCookies(): MultiValueMap<String, ResponseCookie>? {
        return webClient.post()
            .uri {
                it.path(KyoboBookRequestNames.loginUrl)
                    .queryParam(KyoboBookRequestNames.username, username)
                    .queryParam(KyoboBookRequestNames.password, password)
                    .build()
            }
            .exchangeToMono { response -> Mono.just(response.cookies()) }
            .block()
    }
}