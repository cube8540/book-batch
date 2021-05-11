package cube8540.book.batch.infra.kyobo.kr

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.external.BookDocumentMapper
import cube8540.book.batch.external.exception.ExternalException
import cube8540.book.batch.infra.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.isbn
import cube8540.book.batch.infra.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.responseBody
import io.mockk.every
import io.mockk.mockk
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.web.reactive.function.client.WebClient

class KyoboWebClientBookProcessorTest {

    private val document: Document = mockk(relaxed = true)

    private val mockWebServer = MockWebServer()
    private val webClient = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .codecs { it.defaultCodecs().maxInMemorySize(-1) }
        .build()
    private val bookDocumentMapper: BookDocumentMapper = mockk(relaxed = true)

    private val processor = KyoboWebClientBookProcessor(webClient, bookDocumentMapper)

    @BeforeAll
    fun startup() {
        Mockito.mockStatic(Jsoup::class.java).`when`<Document> { Jsoup.parse(responseBody) }.thenReturn(document)
    }

    @Test
    fun `throws exception during parse document`() {
        val bookDetails = BookDetails(isbn)

        val expectedPath = "${KyoboBookRequestNames.kyoboBookDetailsPath}?${KyoboBookRequestNames.isbn}=${isbn}"
        val mockResponse = MockResponse().setBody(responseBody)

        every { bookDocumentMapper.convertValue(document) } throws ExternalException("TEST")
        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = when (request.path!! == expectedPath) {
                true -> mockResponse
                else -> MockResponse().setResponseCode(404)
            }
        }

        val result = processor.process(bookDetails)
        assertThat(result).isNull()
    }

    @Test
    fun `book details processing`() {
        val bookDetails = BookDetails(isbn)

        val expectedPath = "${KyoboBookRequestNames.kyoboBookDetailsPath}?${KyoboBookRequestNames.isbn}=${isbn}"
        val mockResponse = MockResponse().setBody(responseBody)

        val mappedBook = BookDetails(isbn)

        every { bookDocumentMapper.convertValue(document) } returns mappedBook
        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = when (request.path!! == expectedPath) {
                true -> mockResponse
                else -> MockResponse().setResponseCode(404)
            }
        }

        val result = processor.process(bookDetails)
        assertThat(result).isEqualTo(mappedBook)
    }
}