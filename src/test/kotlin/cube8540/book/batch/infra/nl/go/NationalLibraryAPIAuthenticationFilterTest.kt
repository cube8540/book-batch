package cube8540.book.batch.infra.nl.go

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono
import java.net.URI

class NationalLibraryAPIAuthenticationFilterTest {

    private val exchangeFilter = NationalLibraryAPIAuthenticationFilter("secret-key")

    @Test
    fun `add authentication parameter`() {
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
        assertThat(capture.captured.url())
            .isEqualTo(URI.create("http://localhost?${NationalLibraryAPIRequestNames.secretKey}=secret-key"))
    }

}