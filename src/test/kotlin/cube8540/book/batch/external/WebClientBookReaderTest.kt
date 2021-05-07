package cube8540.book.batch.external

import cube8540.book.batch.external.WebClientBookReaderTestEnvironment.bookDetails
import cube8540.book.batch.external.WebClientBookReaderTestEnvironment.endpoint
import cube8540.book.batch.external.WebClientBookReaderTestEnvironment.errorCode
import cube8540.book.batch.external.WebClientBookReaderTestEnvironment.errorMessage
import cube8540.book.batch.external.WebClientBookReaderTestEnvironment.kotlinObjectMapper
import cube8540.book.batch.external.WebClientBookReaderTestEnvironment.mockErrorResponse
import cube8540.book.batch.external.WebClientBookReaderTestEnvironment.mockResponse
import cube8540.book.batch.external.WebClientBookReaderTestEnvironment.pageRequestName
import cube8540.book.batch.external.WebClientBookReaderTestEnvironment.pageSizeRequestName
import cube8540.book.batch.external.exception.ErrorCodeExternalExceptionCreator
import cube8540.book.batch.external.exception.InternalBadRequestException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import org.springframework.web.util.UriBuilderFactory
import java.net.URI

class WebClientBookReaderTest {

    private val mockWebServer = MockWebServer()

    private val uriBuilderFactory: UriBuilderFactory = mockk(relaxed = true)
    private val exceptionCreator: ErrorCodeExternalExceptionCreator = mockk(relaxed = true)
    private val webClient = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .exchangeStrategies(
            ExchangeStrategies.builder().codecs {
                it.customCodecs().register(Jackson2JsonEncoder(kotlinObjectMapper))
                it.customCodecs().register(Jackson2JsonDecoder(kotlinObjectMapper))
            }
            .build()
        )
        .build()

    private val webClientBookReader = WebClientBookReader(uriBuilderFactory, webClient)

    init {
        webClientBookReader.requestPageParameterName = pageRequestName
        webClientBookReader.requestPageSizeParameterName = pageSizeRequestName
        webClientBookReader.exceptionCreator = exceptionCreator
    }

    @Test
    fun `read value`() {
        val uriBuilder: UriBuilder = mockk(relaxed = true)

        every { uriBuilderFactory.builder() } returns uriBuilder
        every { uriBuilder.queryParam(pageRequestName, webClientBookReader.page + 1) } returns uriBuilder
        every { uriBuilder.queryParam(pageSizeRequestName, webClientBookReader.pageSize) } returns uriBuilder
        every { uriBuilder.build() } returns URI.create(mockWebServer.url(endpoint).toString())

        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = when (request.path!! == endpoint) {
                true -> mockResponse
                else -> MockResponse().setResponseCode(404)
            }
        }

        val result = webClientBookReader.read()
        verifyOrder {
            // read 메소드가 작동하면서 page에 1을 더하기 때문에 위에서는 페이지 값 그대로 비교한다.
            uriBuilder.queryParam(pageRequestName, webClientBookReader.page)
            uriBuilder.queryParam(pageSizeRequestName, webClientBookReader.pageSize)
            uriBuilder.build()
        }
        assertThat(result).isEqualTo(bookDetails)
    }

    @Test
    fun `exception during to read`() {
        val uriBuilder: UriBuilder = mockk(relaxed = true)

        every { uriBuilderFactory.builder() } returns uriBuilder
        every { uriBuilder.queryParam(pageRequestName, webClientBookReader.page + 1) } returns uriBuilder
        every { uriBuilder.queryParam(pageSizeRequestName, webClientBookReader.pageSize) } returns uriBuilder
        every { uriBuilder.build() } returns URI.create(mockWebServer.url(endpoint).toString())

        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = when (request.path!! == endpoint) {
                true -> mockErrorResponse
                else -> MockResponse().setResponseCode(200)
            }
        }
        every { exceptionCreator.create(errorCode, errorMessage) } returns InternalBadRequestException(errorMessage)

        val thrown = catchThrowable { webClientBookReader.read() }
        assertThat(thrown).isInstanceOf(InternalBadRequestException::class.java)
        assertThat((thrown as InternalBadRequestException).message).isEqualTo(errorMessage)
    }

    @AfterAll
    fun cleanup() {
        mockWebServer.shutdown()
        mockWebServer.close()
    }

}