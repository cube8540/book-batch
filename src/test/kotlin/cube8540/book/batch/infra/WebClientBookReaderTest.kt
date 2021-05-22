package cube8540.book.batch.infra

import cube8540.book.batch.external.PageDecision
import cube8540.book.batch.external.exception.ErrorCodeExternalExceptionCreator
import cube8540.book.batch.external.exception.InternalBadRequestException
import cube8540.book.batch.infra.WebClientBookReaderTestEnvironment.bookDetailsContext
import cube8540.book.batch.infra.WebClientBookReaderTestEnvironment.createWebClient
import cube8540.book.batch.infra.WebClientBookReaderTestEnvironment.endpoint
import cube8540.book.batch.infra.WebClientBookReaderTestEnvironment.errorCode
import cube8540.book.batch.infra.WebClientBookReaderTestEnvironment.errorMessage
import cube8540.book.batch.infra.WebClientBookReaderTestEnvironment.mockEmptyResponse
import cube8540.book.batch.infra.WebClientBookReaderTestEnvironment.mockErrorResponse
import cube8540.book.batch.infra.WebClientBookReaderTestEnvironment.mockSuccessfulResponse
import cube8540.book.batch.infra.WebClientBookReaderTestEnvironment.pageRequestName
import cube8540.book.batch.infra.WebClientBookReaderTestEnvironment.pageSizeRequestName
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import okhttp3.mockwebserver.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import java.net.URI
import kotlin.random.Random

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class WebClientBookReaderTest {

    private val mockWebServer = MockWebServer()

    private val uriBuilder: UriBuilder = mockk(relaxed = true)
    private val exceptionCreator: ErrorCodeExternalExceptionCreator = mockk(relaxed = true)

    private val webClient: WebClient = createWebClient(mockWebServer)
    private val pageDecision: PageDecision = mockk(relaxed = true)

    private val webClientBookReader = WebClientBookReader(uriBuilder, webClient)

    init {
        webClientBookReader.requestPageParameterName = pageRequestName
        webClientBookReader.requestPageSizeParameterName = pageSizeRequestName
        webClientBookReader.exceptionCreator = exceptionCreator
        webClientBookReader.pageSize = 1
        webClientBookReader.pageDecision = pageDecision
    }

    @Test
    fun `read value`() {
        val randomPage = Random.nextInt()

        every { pageDecision.calculation(webClientBookReader.page + 1, webClientBookReader.pageSize) } returns randomPage
        every { uriBuilder.replaceQueryParam(pageRequestName, randomPage) } returns uriBuilder
        every { uriBuilder.replaceQueryParam(pageSizeRequestName, webClientBookReader.pageSize) } returns uriBuilder
        every { uriBuilder.build() } returns URI.create(mockWebServer.url(endpoint).toString())
        configSuccessfulHttpResponse(mockSuccessfulResponse, endpoint)

        val result = webClientBookReader.read()
        verifyOrder {
            uriBuilder.replaceQueryParam(pageRequestName, randomPage)
            uriBuilder.replaceQueryParam(pageSizeRequestName, webClientBookReader.pageSize)
            uriBuilder.build()
        }
        assertThat(result).isEqualTo(bookDetailsContext)
    }

    @Test
    fun `api returns empty data when results already not empty`() {
        val randomPage = Random.nextInt()

        every { pageDecision.calculation(webClientBookReader.page + 1, webClientBookReader.pageSize) } returns randomPage
        every { uriBuilder.replaceQueryParam(pageRequestName, randomPage) } returns uriBuilder
        every { uriBuilder.replaceQueryParam(pageSizeRequestName, webClientBookReader.pageSize) } returns uriBuilder
        every { uriBuilder.build() } returns URI.create(mockWebServer.url(endpoint).toString())
        configSuccessfulHttpResponse(mockSuccessfulResponse, endpoint)
        webClientBookReader.read()

        val secondRandomPage = Random.nextInt()
        every { pageDecision.calculation(webClientBookReader.page + 1, webClientBookReader.pageSize) } returns secondRandomPage
        every { uriBuilder.replaceQueryParam(pageRequestName, secondRandomPage) } returns uriBuilder
        every { uriBuilder.replaceQueryParam(pageSizeRequestName, webClientBookReader.pageSize) } returns uriBuilder
        every { uriBuilder.build() } returns URI.create(mockWebServer.url(endpoint).toString())
        configSuccessfulHttpResponse(mockEmptyResponse, endpoint)

        val result = webClientBookReader.read()
        assertThat(result).isNull()
    }

    @Test
    fun `exception during to read`() {
        val randomPage = Random.nextInt()

        every { pageDecision.calculation(webClientBookReader.page + 1, webClientBookReader.pageSize) } returns randomPage
        every { uriBuilder.replaceQueryParam(pageRequestName, randomPage) } returns uriBuilder
        every { uriBuilder.replaceQueryParam(pageSizeRequestName, webClientBookReader.pageSize) } returns uriBuilder
        every { uriBuilder.build() } returns URI.create(mockWebServer.url(endpoint).toString())

        configFailsHttpResponse(mockErrorResponse, endpoint)
        every { exceptionCreator.create(errorCode, errorMessage) } returns InternalBadRequestException(errorMessage)

        val thrown = catchThrowable { webClientBookReader.read() }
        assertThat(thrown).isInstanceOf(InternalBadRequestException::class.java)
        assertThat((thrown as InternalBadRequestException).message).isEqualTo(errorMessage)
    }

    @Test
    fun `retry timeout`() {
        val randomRetryCount = Random.nextInt(2, 5)
        val randomPage = Random.nextInt()

        webClientBookReader.retryCount = randomRetryCount
        webClientBookReader.retryDelaySecond = 1

        IntRange(0, randomRetryCount - 1).forEach { _ -> mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)) }
        mockWebServer.enqueue(mockSuccessfulResponse)

        every { pageDecision.calculation(webClientBookReader.page + 1, webClientBookReader.pageSize) } returns randomPage
        every { uriBuilder.replaceQueryParam(pageRequestName, randomPage) } returns uriBuilder
        every { uriBuilder.replaceQueryParam(pageSizeRequestName, webClientBookReader.pageSize) } returns uriBuilder
        every { uriBuilder.build() } returns URI.create(mockWebServer.url(endpoint).toString())

        val result = webClientBookReader.read()
        verifyOrder {
            uriBuilder.replaceQueryParam(pageRequestName, randomPage)
            uriBuilder.replaceQueryParam(pageSizeRequestName, webClientBookReader.pageSize)
            uriBuilder.build()
        }
        assertThat(result).isEqualTo(bookDetailsContext)
    }

    @AfterEach
    fun cleanup() {
        mockWebServer.shutdown()
        mockWebServer.close()
    }

    private fun configSuccessfulHttpResponse(response: MockResponse, path: String) {
        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = when (request.path == path) {
                true -> response
                else -> MockResponse().setResponseCode(404)
            }
        }
    }

    private fun configFailsHttpResponse(response: MockResponse, path: String) {
        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = when (request.path == path) {
                true -> response
                else -> MockResponse().setResponseCode(200)
            }
        }
    }
}