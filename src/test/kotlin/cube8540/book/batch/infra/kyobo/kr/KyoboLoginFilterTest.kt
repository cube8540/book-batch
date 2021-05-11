package cube8540.book.batch.infra.kyobo.kr

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.cookie0001
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.cookie0002
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.cookie0003
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.cookieKey0001
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.cookieKey0002
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.cookieKey0003
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.cookieValue0001
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.cookieValue0002
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.cookieValue0003
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.expiredDateTime
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.notExpiredDateTime
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.now
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.password
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.setCookieHeader
import cube8540.book.batch.infra.kyobo.kr.KyoboLoginFilterTestEnvironment.username
import cube8540.book.batch.toDefaultInstance
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Clock

class KyoboLoginFilterTest {
    private val mockWebserver = MockWebServer()

    private val resultCookies = HashMap<String, String>()

    private val webClient: WebClient = WebClient.builder()
        .baseUrl(mockWebserver.url("/").toString())
        .build()

    private val kyoboLoginFilter = KyoboLoginFilter(username, password, webClient)

    init {
        resultCookies[cookieKey0001] = cookieValue0001
        resultCookies[cookieKey0002] = cookieValue0002
        resultCookies[cookieKey0003] = cookieValue0003
    }

    @Test
    fun `set authentication header when login info is null`() {
        val mockResponse = MockResponse()
            .addHeader(setCookieHeader, cookie0001)
            .addHeader(setCookieHeader, cookie0002)
            .addHeader(setCookieHeader, cookie0003)
        val exchangeResult: Mono<ClientResponse> = mockk(relaxed = true)
        val capture = slot<ClientRequest>()
        val clientRequest: ClientRequest = mockk(relaxed = true) {
            every { method() } returns HttpMethod.GET
            every { url() } returns URI.create("http://localhost")
        }
        val nextFunction: ExchangeFunction = mockk(relaxed = true) {
            every { exchange(capture(capture)) } returns exchangeResult
        }
        val expectedLoginPath = "${KyoboBookRequestNames.loginUrl}?${KyoboBookRequestNames.username}=${username}&${KyoboBookRequestNames.password}=${password}"

        kyoboLoginFilter.loginInfo = null
        mockWebserver.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = when (request.path == expectedLoginPath) {
                true -> mockResponse
                else -> MockResponse().setResponseCode(404)
            }
        }
        KyoboLoginFilter.clock = Clock.fixed(now.toDefaultInstance(), BatchApplication.DEFAULT_TIME_ZONE.toZoneId())

        val result = kyoboLoginFilter.filter(clientRequest, nextFunction)
        assertThat(result).isEqualTo(exchangeResult)
        assertThat(capture.captured.cookies()[cookieKey0001]).isEqualTo(listOf(cookieValue0001))
        assertThat(capture.captured.cookies()[cookieKey0002]).isEqualTo(listOf(cookieValue0002))
        assertThat(capture.captured.cookies()[cookieKey0003]).isEqualTo(listOf(cookieValue0003))
        assertThat(kyoboLoginFilter.loginInfo!!.cookies).isEqualTo(resultCookies)
        assertThat(kyoboLoginFilter.loginInfo!!.issuedDateTime).isEqualTo(now)
    }

    @Test
    fun `set authentication header when login info is expired`() {
        val mockResponse = MockResponse()
            .addHeader(setCookieHeader, cookie0001)
            .addHeader(setCookieHeader, cookie0002)
            .addHeader(setCookieHeader, cookie0003)
        val exchangeResult: Mono<ClientResponse> = mockk(relaxed = true)
        val capture = slot<ClientRequest>()
        val clientRequest: ClientRequest = mockk(relaxed = true) {
            every { method() } returns HttpMethod.GET
            every { url() } returns URI.create("http://localhost")
        }
        val nextFunction: ExchangeFunction = mockk(relaxed = true) {
            every { exchange(capture(capture)) } returns exchangeResult
        }
        val expectedLoginPath = "${KyoboBookRequestNames.loginUrl}?${KyoboBookRequestNames.username}=${username}&${KyoboBookRequestNames.password}=${password}"

        kyoboLoginFilter.loginInfo = mockk(relaxed = true) {
            every { issuedDateTime } returns now
        }
        mockWebserver.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = when (request.path == expectedLoginPath) {
                true -> mockResponse
                else -> MockResponse().setResponseCode(404)
            }
        }
        KyoboLoginFilter.clock = Clock.fixed(expiredDateTime.toDefaultInstance(), BatchApplication.DEFAULT_TIME_ZONE.toZoneId())

        val result = kyoboLoginFilter.filter(clientRequest, nextFunction)
        assertThat(result).isEqualTo(exchangeResult)
        assertThat(capture.captured.cookies()[cookieKey0001]).isEqualTo(listOf(cookieValue0001))
        assertThat(capture.captured.cookies()[cookieKey0002]).isEqualTo(listOf(cookieValue0002))
        assertThat(capture.captured.cookies()[cookieKey0003]).isEqualTo(listOf(cookieValue0003))
        assertThat(kyoboLoginFilter.loginInfo!!.cookies).isEqualTo(resultCookies)
        assertThat(kyoboLoginFilter.loginInfo!!.issuedDateTime).isEqualTo(expiredDateTime)
    }

    @Test
    fun `set authentication header when login info is not expired`() {
        val exchangeResult: Mono<ClientResponse> = mockk(relaxed = true)
        val capture = slot<ClientRequest>()
        val clientRequest: ClientRequest = mockk(relaxed = true) {
            every { method() } returns HttpMethod.GET
            every { url() } returns URI.create("http://localhost")
        }
        val nextFunction: ExchangeFunction = mockk(relaxed = true) {
            every { exchange(capture(capture)) } returns exchangeResult
        }
        val oldCookies = HashMap<String, String>()

        kyoboLoginFilter.loginInfo = mockk(relaxed = true) {
            every { cookies } returns oldCookies
            every { issuedDateTime } returns now
        }
        KyoboLoginFilter.clock = Clock.fixed(notExpiredDateTime.toDefaultInstance(), BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
        oldCookies[cookieKey0001] = "oldCookieValue0001"
        oldCookies[cookieKey0002] = "oldCookieValue0002"
        oldCookies[cookieKey0003] = "oldCookieValue0003"

        val result = kyoboLoginFilter.filter(clientRequest, nextFunction)
        assertThat(result).isEqualTo(exchangeResult)
        assertThat(capture.captured.cookies()[cookieKey0001]).isEqualTo(listOf("oldCookieValue0001"))
        assertThat(capture.captured.cookies()[cookieKey0002]).isEqualTo(listOf("oldCookieValue0002"))
        assertThat(capture.captured.cookies()[cookieKey0003]).isEqualTo(listOf("oldCookieValue0003"))
        assertThat(kyoboLoginFilter.loginInfo!!.cookies).isEqualTo(oldCookies)
        assertThat(kyoboLoginFilter.loginInfo!!.issuedDateTime).isEqualTo(now)
    }

    @AfterAll
    fun cleanup() {
        mockWebserver.shutdown()
        mockWebserver.close()
    }
}

