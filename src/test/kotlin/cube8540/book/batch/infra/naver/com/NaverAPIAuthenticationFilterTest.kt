package cube8540.book.batch.infra.naver.com

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono
import java.net.URI

class NaverAPIAuthenticationFilterTest {

    private val exchangeFilter = NaverBookAPIAuthenticationFilter("client-id", "client-secret")

    @Test
    fun `add authentication header`() {
        val exchangeResult: Mono<ClientResponse> = mockk(relaxed = true)
        val capture = slot<ClientRequest>()
        val clientRequest: ClientRequest = mockk(relaxed = true) {
            every { method() } returns HttpMethod.GET
            every { url() } returns URI.create("http://localhost")
        }
        val nextFunction: ExchangeFunction = mockk(relaxed = true) {
            every { exchange(capture(capture)) } returns exchangeResult
        }

        val result = exchangeFilter.filter(clientRequest, nextFunction)
        assertThat(result).isEqualTo(exchangeResult)
        assertThat(capture.captured.method()).isEqualTo(HttpMethod.GET)
        assertThat(capture.captured.url()).isEqualTo(URI.create("http://localhost"))
        assertThat(capture.captured.headers()[NaverBookAPIRequestNames.clientId]!!.first()).isEqualTo("client-id")
        assertThat(capture.captured.headers()[NaverBookAPIRequestNames.clientSecret]!!.first()).isEqualTo("client-secret")
    }
}