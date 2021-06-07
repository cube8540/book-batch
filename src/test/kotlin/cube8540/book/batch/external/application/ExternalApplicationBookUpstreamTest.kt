package cube8540.book.batch.external.application

import cube8540.book.batch.EndpointProperty
import io.mockk.every
import io.mockk.mockk
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod.POST
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

class ExternalApplicationBookUpstreamTest {

    private val mockWebServer = MockWebServer()

    private val webClient = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .exchangeStrategies(
            ExchangeStrategies.builder().codecs {
                it.customCodecs().register(Jackson2JsonDecoder(defaultObjectMapper))
                it.customCodecs().register(Jackson2JsonEncoder(defaultObjectMapper))
            }.build()
        )
        .build()

    private val upstreamEndpoint = "/upstreamEndpoint"
    private val endpointProperty: EndpointProperty = mockk {
        every { application } returns mockk {
            every { upstream } returns upstreamEndpoint
        }
    }

    private val applicationUpstream = ExternalApplicationBookUpstream(webClient, endpointProperty)

    @Test
    fun `upstream book fails`() {
        val request = createUpstreamBook(
            createUpstreamBookDetails(isbn = "isbn00000"),
            createUpstreamBookDetails(isbn = "isbn00001"),
            createUpstreamBookDetails(isbn = "isbn00002")
        )
        val httpResponse = MockResponse().setResponseCode(400)

        mockWebServer.dispatcher = createExternalUpstreamDispatcher(params = request, path = endpointProperty.application, result = httpResponse)

        assertThatThrownBy { applicationUpstream.upstream(request) }
            .isInstanceOf(ExternalUpstreamFailsExceptions::class.java)

        val requested = mockWebServer.takeRequest()
        assertThat(requested.method).isEqualTo(POST.toString())
    }

    @Test
    fun `upstream book`() {
        val request = createUpstreamBook(
            createUpstreamBookDetails(isbn = "isbn00000"),
            createUpstreamBookDetails(isbn = "isbn00001"),
            createUpstreamBookDetails(isbn = "isbn00002")
        )
        val httpResponse = MockResponse().setResponseCode(200)

        mockWebServer.dispatcher = createExternalUpstreamDispatcher(params = request, path = endpointProperty.application, result = httpResponse)

        assertThatCode { applicationUpstream.upstream(request) }.doesNotThrowAnyException()

        val requested = mockWebServer.takeRequest()
        assertThat(requested.method).isEqualTo(POST.toString())
    }
}