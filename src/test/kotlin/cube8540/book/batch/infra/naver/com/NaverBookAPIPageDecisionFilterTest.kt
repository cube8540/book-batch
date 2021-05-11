package cube8540.book.batch.infra.naver.com

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono
import java.net.URI
import java.util.stream.Stream
import kotlin.random.Random

class NaverBookAPIPageDecisionFilterTest {

    private val exchangeFilter = NaverBookAPIPageDecisionFilter()

    @Test
    fun `add page number when request first page`() {
        val oldRequestUrl = "http://localhost?${NaverBookAPIRequestNames.start}=1&${NaverBookAPIRequestNames.display}=100"
        val exchangeResult: Mono<ClientResponse> = mockk(relaxed = true)
        val capture = slot<ClientRequest>()
        val clientRequest: ClientRequest = mockk(relaxed = true) {
            every { method() } returns HttpMethod.GET
            every { url() } returns URI.create(oldRequestUrl)
        }
        val nextFunction: ExchangeFunction = mockk(relaxed = true) {
            every { exchange(capture(capture)) } returns exchangeResult
        }

        val result = exchangeFilter.filter(clientRequest, nextFunction)
        assertThat(result).isEqualTo(exchangeResult)
        assertThat(capture.captured.method()).isEqualTo(HttpMethod.GET)
        assertThat(capture.captured.url()).isEqualTo(URI.create(oldRequestUrl))
    }

    @ParameterizedTest
    @MethodSource(value = ["randomPageProvider"])
    fun `add page number when request not first page`(start: Int, display: Int) {
        val oldRequestUrl = "http://localhost?${NaverBookAPIRequestNames.start}=${start}&${NaverBookAPIRequestNames.display}=${display}"
        val newRequestUrl = "http://localhost?${NaverBookAPIRequestNames.start}=${start + display}&${NaverBookAPIRequestNames.display}=${display}"
        val exchangeResult: Mono<ClientResponse> = mockk(relaxed = true)
        val capture = slot<ClientRequest>()
        val clientRequest: ClientRequest = mockk(relaxed = true) {
            every { method() } returns HttpMethod.GET
            every { url() } returns URI.create(oldRequestUrl)
        }
        val nextFunction: ExchangeFunction = mockk(relaxed = true) {
            every { exchange(capture(capture)) } returns exchangeResult
        }

        val result = exchangeFilter.filter(clientRequest, nextFunction)
        assertThat(result).isEqualTo(exchangeResult)
        assertThat(capture.captured.method()).isEqualTo(HttpMethod.GET)
        assertThat(capture.captured.url()).isEqualTo(URI.create(newRequestUrl))
    }

    private fun randomPageProvider(): Stream<Arguments> = Stream.of(Arguments.of(Random.nextInt(0, 100), Random.nextInt(0, 100)))
}