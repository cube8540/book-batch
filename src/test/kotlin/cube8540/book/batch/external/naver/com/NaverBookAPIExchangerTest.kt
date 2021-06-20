package cube8540.book.batch.external.naver.com

import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.book.domain.defaultPublisher
import cube8540.book.batch.external.BookAPIRequest
import cube8540.book.batch.external.PageDecision
import cube8540.book.batch.external.exception.ErrorCodeExternalExceptionCreator
import cube8540.book.batch.external.exception.InvalidAuthenticationException
import io.mockk.every
import io.mockk.mockk
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.SocketPolicy
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.time.LocalDate
import kotlin.random.Random

class NaverBookAPIExchangerTest {

    private val randomPage = Random.nextInt()
    private val randomSize = Random.nextInt()
    private val realRequestedPage = Random.nextInt(1, 999)

    private val from = LocalDate.of(2021,  5, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val mockWebServer = MockWebServer()
    private val publisherMapper: PublisherRawMapper = mockk(relaxed = true)

    private val pageDecision: PageDecision = mockk(relaxed = true)
    private val exceptionCreator: ErrorCodeExternalExceptionCreator = mockk(relaxed = true)

    private val exchanger = NaverBookAPIExchanger(
        createNaverWebClient(mockWebServer, publisherMapper),
        NaverBookAPIKey(defaultClientId, defaultClientSecret)
    )

    init {
        exchanger.pageDecision = pageDecision
        exchanger.exceptionCreator = exceptionCreator
    }

    @Test
    fun `exchange book api`() {
        val bookArray = createBookJsonArrayNode(createBookJsonNode(isbn = "isbn00000"), createBookJsonNode(isbn = "isbn00001"), createBookJsonNode(isbn = "isbn00002"))
        val bookAPIResponse = createNaverBookAPIResponse(total = 3, display = 3, start = realRequestedPage, items = bookArray)

        val httpResponse = MockResponse().setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(bookAPIResponse.toString())
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        every { pageDecision.calculation(randomPage, randomSize) } returns realRequestedPage
        mockWebServer.dispatcher = createNaverBookAPIDispatcher(params = exchangeParameter, realRequestedPage = realRequestedPage, result = httpResponse)

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result.totalCount).isEqualTo(3)
        assertThat(result.page).isEqualTo(realRequestedPage.toLong())
        assertThat(result.books.size).isEqualTo(3)
        assertThat(result.books).containsExactly(
            NaverBookAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00000"), publisherMapper),
            NaverBookAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00001"), publisherMapper),
            NaverBookAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00002"), publisherMapper)
        )
    }

    @Test
    fun `exchange book api when requested page greater then 999`() {
        val page = 1000
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        every { pageDecision.calculation(randomPage, randomSize) } returns page

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result.totalCount).isZero
        assertThat(result.page).isZero
        assertThat(result.books).isEmpty()
    }

    @Test
    fun `retry timeout`() {
        val randomRetryCount = Random.nextInt(2, 5)

        val bookArray = createBookJsonArrayNode(createBookJsonNode(isbn = "isbn00000"), createBookJsonNode(isbn = "isbn00001"), createBookJsonNode(isbn = "isbn00002"))
        val bookAPIResponse = createNaverBookAPIResponse(total = 3, display = 3, start = realRequestedPage, items = bookArray)

        val httpResponse = MockResponse().setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(bookAPIResponse.toString())
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        exchanger.retryCount = randomRetryCount
        exchanger.retryDelaySecond = 0

        every { pageDecision.calculation(randomPage, randomSize) } returns realRequestedPage
        mockWebServer.dispatcher = QueueDispatcher()
        IntRange(0, randomRetryCount - 1).forEach { _ -> mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)) }
        mockWebServer.enqueue(httpResponse)

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result.totalCount).isEqualTo(3)
        assertThat(result.page).isEqualTo(realRequestedPage.toLong())
        assertThat(result.books.size).isEqualTo(3)
        assertThat(result.books).containsExactly(
            NaverBookAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00000"), publisherMapper),
            NaverBookAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00001"), publisherMapper),
            NaverBookAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00002"), publisherMapper)
        )
    }

    @Test
    fun `exchange book when api return error`() {
        val bookAPIResponse = createNaverBookErrorResponse()

        val httpResponse = MockResponse().setResponseCode(400)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(bookAPIResponse.toString())
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        mockWebServer.dispatcher = createNaverBookAPIDispatcher(params = exchangeParameter, realRequestedPage = realRequestedPage, result = httpResponse)
        every { pageDecision.calculation(randomPage, randomSize) } returns realRequestedPage
        every { exceptionCreator.create(defaultErrorCode, defaultErrorMessage) } returns
                InvalidAuthenticationException(defaultErrorMessage)

        assertThatThrownBy { exchanger.exchange(exchangeParameter) }
            .isInstanceOf(InvalidAuthenticationException::class.java)
            .hasMessage(defaultErrorMessage)
    }

}