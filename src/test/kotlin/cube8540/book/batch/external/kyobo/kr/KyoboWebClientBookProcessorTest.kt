package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController
import cube8540.book.batch.book.domain.createBookContext
import cube8540.book.batch.book.domain.createBookDetails
import cube8540.book.batch.external.BookDocumentMapper
import cube8540.book.batch.interlock.client.ClientExchangeException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.SocketPolicy
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Test
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration
import kotlin.random.Random

class KyoboWebClientBookProcessorTest {

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
    }

    @Test
    fun `throws exception during parse document`() {
        val responseBody = createDocument().html()
        val responseBodyCaptor = slot<Document>()

        val bookDetails: BookDetails = createBookDetails()
        val bookDetailsPageResponse = MockResponse().setBody(responseBody)

        every { bookDocumentMapper.convertValue(capture(responseBodyCaptor)) } throws ClientExchangeException("TEST")
        mockWebServer.dispatcher = createKyoboBookRequestDispatcher(result = bookDetailsPageResponse)

        val result = processor.process(bookDetails)
        val expectedDocument = Jsoup.parse(responseBody)
            .outputSettings(Document.OutputSettings().prettyPrint(false))

        assertThat(responseBodyCaptor.captured.outputSettings().prettyPrint())
            .isEqualTo(expectedDocument.outputSettings().prettyPrint())
        assertThat(responseBodyCaptor.captured.html())
            .isEqualTo(expectedDocument.html())
        assertThat(result).isNull()
    }

    @Test
    fun `book details processing`() {
        val responseBody = createDocument().html()
        val responseBodyCaptor = slot<Document>()

        val bookContext = createBookContext(isbn = "extractedIsbn")
        val bookDetails = createBookDetails(isbn = "originalIsbn")
        val mergeCompletedResult: BookDetails = mockk(relaxed = true)

        val mergeItemCaptor = slot<BookDetails>()
        val bookDetailsPageResponse = MockResponse().setBody(responseBody)

        every { bookDocumentMapper.convertValue(capture(responseBodyCaptor)) } returns bookContext
        every { controller.merge(bookDetails, capture(mergeItemCaptor)) } returns mergeCompletedResult
        mockWebServer.dispatcher = createKyoboBookRequestDispatcher(isbn = "originalIsbn", result = bookDetailsPageResponse)

        val result = processor.process(bookDetails)
        val expectedDocument = Jsoup.parse(responseBody)
            .outputSettings(Document.OutputSettings().prettyPrint(false))

        assertThat(responseBodyCaptor.captured.outputSettings().prettyPrint())
            .isEqualTo(expectedDocument.outputSettings().prettyPrint())
        assertThat(responseBodyCaptor.captured.html())
            .isEqualTo(expectedDocument.html())
        assertThat(result).isEqualTo(mergeCompletedResult)
        assertThat(mergeItemCaptor.captured).isEqualTo(createBookDetails(isbn = "extractedIsbn"))
    }

    @Test
    fun `retry timeout`() {
        val randomRetryCount = Random.nextInt(2, 5)

        val responseBody = createDocument().html()
        val responseBodyCaptor = slot<Document>()

        val bookContext = createBookContext(isbn = "extractedIsbn")
        val bookDetails = createBookDetails(isbn = "originalIsbn")
        val mergeCompletedResult: BookDetails = mockk(relaxed = true)

        val mergeItemCaptor = slot<BookDetails>()
        val bookDetailsPageResponse = MockResponse().setBody(responseBody)

        processor.retryCount = randomRetryCount
        processor.retryDelaySecond = 0

        mockWebServer.dispatcher = QueueDispatcher()
        IntRange(0, randomRetryCount - 1).forEach { _ -> mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)) }
        mockWebServer.enqueue(bookDetailsPageResponse)

        every { bookDocumentMapper.convertValue(capture(responseBodyCaptor)) } returns bookContext
        every { controller.merge(bookDetails, capture(mergeItemCaptor)) } returns mergeCompletedResult

        val result = processor.process(bookDetails)
        val expectedDocument = Jsoup.parse(responseBody)
            .outputSettings(Document.OutputSettings().prettyPrint(false))

        assertThat(result).isEqualTo(mergeCompletedResult)
        assertThat(responseBodyCaptor.captured.outputSettings().prettyPrint())
            .isEqualTo(expectedDocument.outputSettings().prettyPrint())
        assertThat(responseBodyCaptor.captured.html())
            .isEqualTo(expectedDocument.html())
        assertThat(mergeItemCaptor.captured).isEqualTo(createBookDetails(isbn = "extractedIsbn"))
    }
}