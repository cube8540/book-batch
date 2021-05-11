package cube8540.book.batch.infra.kyobo.kr

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.external.BookDocumentMapper
import cube8540.book.batch.external.exception.ExternalException
import cube8540.book.batch.infra.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.author
import cube8540.book.batch.infra.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.categories
import cube8540.book.batch.infra.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.description
import cube8540.book.batch.infra.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.isbn
import cube8540.book.batch.infra.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.largeThumbnail
import cube8540.book.batch.infra.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.mediumThumbnail
import cube8540.book.batch.infra.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.originalPrice
import cube8540.book.batch.infra.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.responseBody
import cube8540.book.batch.infra.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.seriesBarcode
import cube8540.book.batch.infra.kyobo.kr.KyoboWebClientBookProcessorTestEnvironment.title
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

        mappedBook.title = title
        mappedBook.authors = author
        mappedBook.largeThumbnail = largeThumbnail
        mappedBook.mediumThumbnail = mediumThumbnail
        mappedBook.price = originalPrice
        mappedBook.seriesCode = seriesBarcode
        mappedBook.divisions = categories
        mappedBook.description = description
        every { bookDocumentMapper.convertValue(document) } returns mappedBook
        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = when (request.path!! == expectedPath) {
                true -> mockResponse
                else -> MockResponse().setResponseCode(404)
            }
        }

        val result = processor.process(bookDetails)!!
        assertThat(result.title).isEqualTo(title)
        assertThat(result.authors).isEqualTo(author)
        assertThat(result.largeThumbnail).isEqualTo(largeThumbnail)
        assertThat(result.mediumThumbnail).isEqualTo(mediumThumbnail)
        assertThat(result.price).isEqualTo(originalPrice)
        assertThat(result.divisions).isEqualTo(categories)
        assertThat(result.seriesCode).isEqualTo(seriesBarcode)
        assertThat(result.description).isEqualTo(description)
    }
}