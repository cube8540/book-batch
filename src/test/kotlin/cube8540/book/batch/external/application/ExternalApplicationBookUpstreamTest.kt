package cube8540.book.batch.external.application

import cube8540.book.batch.EndpointProperty
import cube8540.book.batch.book.domain.defaultFailedReasonMessage
import cube8540.book.batch.book.domain.defaultFailedReasonProperty
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.POST
import org.springframework.http.MediaType
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
    private val eventPublisher: ApplicationEventPublisher = mockk(relaxed = true)
    private val endpointProperty: EndpointProperty = mockk {
        every { application } returns mockk {
            every { upstream } returns upstreamEndpoint
        }
    }

    private val applicationUpstream = ExternalApplicationBookUpstream(webClient, eventPublisher, endpointProperty)

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

        val responseJson = createUpstreamResponseJson(
            successBooks = listOf("isbn00000", "isbn00001"),
            failedBooks = jsonArrayNode(createUpstreamFailedBooksJson(
                isbn = "isbn00002",
                errors = jsonArrayNode(createUpstreamFailedReasonJson())
            ))
        )
        val httpResponse = MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(responseJson.toString())

        mockWebServer.dispatcher = createExternalUpstreamDispatcher(params = request, path = endpointProperty.application, result = httpResponse)

        val expectedComplectedEvent = ExternalUpstreamCompletedEvent(ExternalUpstreamResponse(
            successBooks = listOf("isbn00000", "isbn00001"),
            failedBooks = listOf(ExternalUpstreamFailedBooks(
                isbn = "isbn00002",
                errors = listOf(ExternalUpstreamFailedReason(defaultFailedReasonProperty, defaultFailedReasonMessage))
            ))
        ))

        assertThatCode { applicationUpstream.upstream(request) }.doesNotThrowAnyException()
        assertThat(mockWebServer.takeRequest().method).isEqualTo(POST.toString())
        verify { eventPublisher.publishEvent(expectedComplectedEvent) }
    }
}