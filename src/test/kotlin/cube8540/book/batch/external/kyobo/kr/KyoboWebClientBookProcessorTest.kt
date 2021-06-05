package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsContext
import cube8540.book.batch.book.domain.BookDetailsController
import cube8540.book.batch.external.BookDocumentMapper
import cube8540.book.batch.external.exception.ExternalException
import cube8540.book.batch.external.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.isbn
import cube8540.book.batch.external.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.responseBody
import cube8540.book.batch.getQueryParams
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.mockwebserver.*
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.net.URI
import java.time.Duration
import kotlin.random.Random

class KyoboWebClientBookProcessorTest {

    private val document: Document = mockk(relaxed = true)

    private val mockWebServer = MockWebServer()
    private val webClient = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .clientConnector(ReactorClientHttpConnector(HttpClient.create().responseTimeout(Duration.ofSeconds(1))))
        .codecs { it.defaultCodecs().maxInMemorySize(-1) }
        .build()
    private val bookDocumentMapper: BookDocumentMapper = mockk(relaxed = true)

    private val controller: BookDetailsController = mockk(relaxed = true)

    private val processor = KyoboWebClientBookProcessor(webClient, bookDocumentMapper)

    init {
        processor.controller = controller
        mockedSettings = Mockito.mockStatic(Jsoup::class.java)
        mockedSettings?.`when`<Document> { Jsoup.parse(responseBody) }?.thenReturn(document)
    }

    companion object {
        private var mockedSettings: MockedStatic<Jsoup>? = null

        @AfterAll
        @JvmStatic
        fun cleanup() {
            mockedSettings?.close()
        }
    }

    @Test
    fun `throws exception during parse document`() {
        val bookDetails: BookDetails = mockk(relaxed = true)
        val mockResponse = MockResponse().setBody(responseBody)

        every { bookDetails.isbn } returns isbn
        every { bookDocumentMapper.convertValue(document) } throws ExternalException("TEST")
        configSuccessfulHttpResponse(mockResponse, isbn)

        val result = processor.process(bookDetails)
        assertThat(result).isNull()
    }

    @Test
    fun `book details processing`() {
        val responseResolvedIsbn = "responseResolvedIsbn"
        val responseResolvedBook: BookDetailsContext = mockk(relaxed = true) {
            every { resolveIsbn() } returns responseResolvedIsbn
        }

        val bookDetails: BookDetails = mockk(relaxed = true)
        val mergedBook: BookDetails = mockk(relaxed = true)
        val captor = slot<BookDetails>()

        val mockResponse = MockResponse().setBody(responseBody)

        every { bookDetails.isbn } returns isbn
        every { bookDocumentMapper.convertValue(document) } returns responseResolvedBook
        every { controller.merge(bookDetails, capture(captor)) } returns mergedBook
        configSuccessfulHttpResponse(mockResponse, isbn)

        val result = processor.process(bookDetails)
        assertThat(result).isEqualTo(mergedBook)
        assertThat(captor.captured.isbn).isEqualTo(responseResolvedIsbn)
    }

    @Test
    fun `retry timeout`() {
        val randomRetryCount = Random.nextInt(2, 5)
        val responseResolvedIsbn = "responseResolvedIsbn"
        val responseResolvedBook: BookDetailsContext = mockk(relaxed = true) {
            every { resolveIsbn() } returns responseResolvedIsbn
        }
        val bookDetails: BookDetails = mockk(relaxed = true)
        val mergedBook: BookDetails = mockk(relaxed = true)
        val captor = slot<BookDetails>()
        val mockResponse = MockResponse().setBody(responseBody)

        processor.retryCount = randomRetryCount
        processor.retryDelaySecond = 0

        mockWebServer.dispatcher = QueueDispatcher()
        IntRange(0, randomRetryCount - 1).forEach { _ -> mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)) }
        mockWebServer.enqueue(mockResponse)

        every { bookDetails.isbn } returns isbn
        every { bookDocumentMapper.convertValue(document) } returns responseResolvedBook
        every { controller.merge(bookDetails, capture(captor)) } returns mergedBook

        val result = processor.process(bookDetails)
        assertThat(result).isEqualTo(mergedBook)
        assertThat(captor.captured.isbn).isEqualTo(responseResolvedIsbn)
    }

    private fun configSuccessfulHttpResponse(response: MockResponse, isbn: String) {
        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val params = URI.create(request.path!!).getQueryParams()
                return if (params[KyoboBookRequestNames.isbn]?.first() == isbn && request.requestUrl!!.toUri().path == KyoboBookRequestNames.kyoboBookDetailsPath) {
                    response
                } else {
                    MockResponse().setResponseCode(404)
                }
            }
        }
    }
}