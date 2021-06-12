package cube8540.book.batch.external.nl.go

import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.book.domain.defaultPublisher
import cube8540.book.batch.external.BookAPIRequest
import cube8540.book.batch.external.PageDecision
import io.mockk.every
import io.mockk.mockk
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.SocketPolicy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.time.LocalDate
import kotlin.random.Random

class NationalLibraryAPIExchangerTest {

    private val randomPage = Random.nextInt()
    private val randomSize = Random.nextInt()
    private val realRequestedPage = Random.nextInt()

    private val from = LocalDate.of(2021,  5, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val mockWebServer = MockWebServer()
    private val publisherMapper: PublisherRawMapper = mockk(relaxed = true)

    private val pageDecision: PageDecision = mockk(relaxed = true) {
        every { calculation(randomPage, randomSize) } returns realRequestedPage
    }
    private val exchanger = NationalLibraryAPIExchanger(
        createNationalLibraryWebClient(mockWebServer, publisherMapper),
        NationalLibraryAPIKey(defaultNationalLibraryClientKey)
    )

    init {
        exchanger.pageDecision = pageDecision
    }

    @Test
    fun `exchange book api`() {
        val bookArray = createBookJsonArrayNode(createBookJsonNode(isbn = "isbn00000"), createBookJsonNode(isbn = "isbn00001"), createBookJsonNode(isbn = "isbn00002"))
        val bookAPIResponse = createNationalLibraryAPIResponse(total = 3, page = realRequestedPage, docs = bookArray)

        val httpResponse = MockResponse().setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(bookAPIResponse.toString())
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        mockWebServer.dispatcher = createNationalLibraryDispatcher(params = exchangeParameter, result = httpResponse, page = realRequestedPage)

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result?.totalCount).isEqualTo(3)
        assertThat(result?.page).isEqualTo(realRequestedPage.toLong())
        assertThat(result?.books).contains(
            NationalLibraryJsonNodeContext(createBookJsonNode(isbn = "isbn00000"), publisherMapper),
            NationalLibraryJsonNodeContext(createBookJsonNode(isbn = "isbn00001"), publisherMapper),
            NationalLibraryJsonNodeContext(createBookJsonNode(isbn = "isbn00002"), publisherMapper)
        )
    }

    @Test
    fun `retry timeout`() {
        val randomRetryCount = Random.nextInt(2, 5)
        val bookArray = createBookJsonArrayNode(createBookJsonNode(isbn = "isbn00000"), createBookJsonNode(isbn = "isbn00001"), createBookJsonNode(isbn = "isbn00002"))
        val bookAPIResponse = createNationalLibraryAPIResponse(total = 3, page = realRequestedPage, docs = bookArray)

        val httpResponse = MockResponse().setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(bookAPIResponse.toString())
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        exchanger.retryCount = randomRetryCount
        exchanger.retryDelaySecond = 0

        mockWebServer.dispatcher = QueueDispatcher()
        IntRange(0, randomRetryCount - 1).forEach { _ -> mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)) }
        mockWebServer.enqueue(httpResponse)

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result?.totalCount).isEqualTo(3)
        assertThat(result?.page).isEqualTo(realRequestedPage.toLong())
        assertThat(result?.books).contains(
            NationalLibraryJsonNodeContext(createBookJsonNode(isbn = "isbn00000"), publisherMapper),
            NationalLibraryJsonNodeContext(createBookJsonNode(isbn = "isbn00001"), publisherMapper),
            NationalLibraryJsonNodeContext(createBookJsonNode(isbn = "isbn00002"), publisherMapper)
        )
    }
}