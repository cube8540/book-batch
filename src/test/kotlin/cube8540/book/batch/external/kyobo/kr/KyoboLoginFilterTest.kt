package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.toDefaultInstance
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Clock

class KyoboLoginFilterTest {
    private val mockWebserver = MockWebServer()
    private val webClient: WebClient = WebClient.builder()
        .baseUrl(mockWebserver.url("/").toString())
        .build()

    private val kyoboLoginFilter = KyoboLoginFilter(defaultKyoboLoginUsername, defaultKyoboLoginPassword, webClient)

    @Test
    fun `set authentication header when login info is null`() {
        val loginResponse = MockResponse().addHeader(defaultHeaderCookieName, createHeaderCookieValue())
        val exchangeResult: Mono<ClientResponse> = mockk(relaxed = true)
        val nextRequestCaptor = slot<ClientRequest>()
        val clientRequest: ClientRequest = mockk(relaxed = true)
        val nextFunction: ExchangeFunction = mockk(relaxed = true) {
            every { exchange(capture(nextRequestCaptor)) } returns exchangeResult
        }

        kyoboLoginFilter.loginInfo = null
        mockWebserver.dispatcher = createKyoboLoginDispatcher(result = loginResponse)
        KyoboLoginFilter.clock = Clock.fixed(defaultKyoboLoginIssuedAt.toDefaultInstance(), BatchApplication.DEFAULT_TIME_ZONE.toZoneId())

        val result = kyoboLoginFilter.filter(clientRequest, nextFunction)
        assertThat(result).isEqualTo(exchangeResult)
        assertThat(nextRequestCaptor.captured.cookies()[defaultHeaderCookieKey]).contains(defaultHeaderCookieValue)
        assertThat(kyoboLoginFilter.loginInfo!!.cookies[defaultHeaderCookieKey]).isEqualTo(defaultHeaderCookieValue)
        assertThat(kyoboLoginFilter.loginInfo!!.issuedDateTime).isEqualTo(defaultKyoboLoginIssuedAt)
    }

    @Test
    fun `set authentication header when login info is expired`() {
        val loginResponse = MockResponse().addHeader(defaultHeaderCookieName, createHeaderCookieValue())
        val exchangeResult: Mono<ClientResponse> = mockk(relaxed = true)
        val nextRequestCaptor = slot<ClientRequest>()
        val clientRequest: ClientRequest = mockk(relaxed = true)
        val nextFunction: ExchangeFunction = mockk(relaxed = true) {
            every { exchange(capture(nextRequestCaptor)) } returns exchangeResult
        }

        kyoboLoginFilter.loginInfo = createKyoboLoginInfo()
        mockWebserver.dispatcher = createKyoboLoginDispatcher(result = loginResponse)
        KyoboLoginFilter.clock = Clock.fixed(defaultKyoboLoginExpiredDateTime.toDefaultInstance(), BatchApplication.DEFAULT_TIME_ZONE.toZoneId())

        val result = kyoboLoginFilter.filter(clientRequest, nextFunction)
        assertThat(result).isEqualTo(exchangeResult)
        assertThat(nextRequestCaptor.captured.cookies()[defaultHeaderCookieKey]).contains(defaultHeaderCookieValue)
        assertThat(kyoboLoginFilter.loginInfo!!.cookies[defaultHeaderCookieKey]).isEqualTo(defaultHeaderCookieValue)
        assertThat(kyoboLoginFilter.loginInfo!!.issuedDateTime).isEqualTo(defaultKyoboLoginExpiredDateTime)
    }

    @Test
    fun `set authentication header when login info is not expired`() {
        val exchangeResult: Mono<ClientResponse> = mockk(relaxed = true)
        val nextRequestCaptor = slot<ClientRequest>()
        val clientRequest: ClientRequest = mockk(relaxed = true)
        val nextFunction: ExchangeFunction = mockk(relaxed = true) {
            every { exchange(capture(nextRequestCaptor)) } returns exchangeResult
        }

        kyoboLoginFilter.loginInfo = createKyoboLoginInfo(cookies = mapOf(defaultHeaderCookieKey to "oldCookieValue0000"))
        KyoboLoginFilter.clock = Clock.fixed(defaultKyoboLoginNotExpiredDateTime.toDefaultInstance(), BatchApplication.DEFAULT_TIME_ZONE.toZoneId())

        val result = kyoboLoginFilter.filter(clientRequest, nextFunction)
        assertThat(result).isEqualTo(exchangeResult)
        assertThat(nextRequestCaptor.captured.cookies()[defaultHeaderCookieKey]).contains("oldCookieValue0000")
        assertThat(kyoboLoginFilter.loginInfo!!.cookies[defaultHeaderCookieKey]).isEqualTo("oldCookieValue0000")
        assertThat(kyoboLoginFilter.loginInfo!!.issuedDateTime).isEqualTo(defaultKyoboLoginIssuedAt)
    }
}

