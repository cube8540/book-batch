package cube8540.book.batch.external.aladin.kr

import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.book.domain.defaultPublisher
import cube8540.book.batch.translator.BookAPIRequest
import cube8540.book.batch.translator.PageDecision
import cube8540.book.batch.external.aladin.kr.AladinAPIExchanger.Companion.maximumDataCount
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
import java.time.temporal.ChronoUnit
import kotlin.random.Random

class AladinAPIExchangerTest {

    private val randomPage = Random.nextInt(1, 4)
    private val randomSize = Random.nextInt(1, 50)
    private val realRequestedPage = Random.nextInt(1, 999)

    private val from = LocalDate.of(2021,  5, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val mockWebServer = MockWebServer()
    private val publisherMapper: PublisherRawMapper = mockk(relaxed = true)

    private val pageDecision: PageDecision = mockk(relaxed = true)

    private val exchanger = AladinAPIExchanger(
        createAladinWebClient(mockWebServer, publisherMapper),
        AladinAuthenticationInfo(defaultTtbKey)
    )

    init {
        exchanger.pageDecision = pageDecision
    }

    @Test
    fun `exchange book api`() {
        val between = ChronoUnit.DAYS.between(from, to)
        val betweenThemRequestDate = from.plusDays(Random.nextLong(0, between))
        val bookArray = createBookJsonArrayNode(
            createBookJsonNode(isbn = "isbn0000", publishDate = betweenThemRequestDate),
            createBookJsonNode(isbn = "isbn0001", publishDate = betweenThemRequestDate),
            createBookJsonNode(isbn = "isbn0002", publishDate = betweenThemRequestDate))
        val bookAPIResponse = createAladinBookAPIResponse(total = 3, start = realRequestedPage, items = bookArray)

        val httpResponse = MockResponse().setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(bookAPIResponse.toString())
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        every { pageDecision.calculation(randomPage, randomSize) } returns realRequestedPage
        mockWebServer.dispatcher = createAladinAPIDispatcher(params = exchangeParameter, realPageRequest = realRequestedPage, result = httpResponse)

        val result = exchanger.exchange(exchangeParameter)!!
        assertThat(result.totalCount).isEqualTo(3)
        assertThat(result.page).isEqualTo(realRequestedPage.toLong())
        assertThat(result.books).containsExactly(
            AladinAPIJsonNodeContext(createBookJsonNode(isbn = "isbn0000", publishDate = betweenThemRequestDate), publisherMapper),
            AladinAPIJsonNodeContext(createBookJsonNode(isbn = "isbn0001", publishDate = betweenThemRequestDate), publisherMapper),
            AladinAPIJsonNodeContext(createBookJsonNode(isbn = "isbn0002", publishDate = betweenThemRequestDate), publisherMapper)
        )
    }

    @Test
    fun `exchange book api when page multiple size is grater then maximum data count`() {
        val page = (maximumDataCount / randomSize) + 1
        val exchangeParameter = BookAPIRequest(page = page, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        val result = exchanger.exchange(exchangeParameter)!!
        assertThat(result.page).isZero
        assertThat(result.totalCount).isZero
        assertThat(result.books).isEmpty()
    }

    @Test
    fun `when book api results contains books not published on then request date`() {
        val between = ChronoUnit.DAYS.between(from, to)
        val betweenThemRequestDate = from.plusDays(Random.nextLong(0, between))
        val beforeRequestDate = from.minusDays(Random.nextLong(1, 100))
        val afterRequestDate = to.plusDays(Random.nextLong(1, 100))

        val bookArray = createBookJsonArrayNode(
            createBookJsonNode(isbn = "isbn0000", publishDate = betweenThemRequestDate),
            createBookJsonNode(isbn = "isbn0001", publishDate = beforeRequestDate),
            createBookJsonNode(isbn = "isbn0002", publishDate = afterRequestDate)
        )
        val bookAPIResponse = createAladinBookAPIResponse(total = 3, start = realRequestedPage, items = bookArray)

        val httpResponse = MockResponse().setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(bookAPIResponse.toString())
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        every { pageDecision.calculation(randomPage, randomSize) } returns realRequestedPage
        mockWebServer.dispatcher = createAladinAPIDispatcher(params = exchangeParameter, realPageRequest = realRequestedPage, result = httpResponse)

        val result = exchanger.exchange(exchangeParameter)!!
        assertThat(result.totalCount).isEqualTo(3)
        assertThat(result.page).isEqualTo(realRequestedPage.toLong())
        assertThat(result.books).containsExactly(
            AladinAPIJsonNodeContext(createBookJsonNode(isbn = "isbn0000", publishDate = betweenThemRequestDate), publisherMapper)
        )
    }

    @Test
    fun `retry timeout`() {
        val randomRetryCount = Random.nextInt(2, 5)

        val between = ChronoUnit.DAYS.between(from, to)
        val betweenThemRequestDate = from.plusDays(Random.nextLong(0, between))
        val bookArray = createBookJsonArrayNode(
            createBookJsonNode(isbn = "isbn0000", publishDate = betweenThemRequestDate),
            createBookJsonNode(isbn = "isbn0001", publishDate = betweenThemRequestDate),
            createBookJsonNode(isbn = "isbn0002", publishDate = betweenThemRequestDate))
        val bookAPIResponse = createAladinBookAPIResponse(total = 3, start = realRequestedPage, items = bookArray)

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

        val result = exchanger.exchange(exchangeParameter)!!
        assertThat(result.totalCount).isEqualTo(3)
        assertThat(result.page).isEqualTo(realRequestedPage.toLong())
        assertThat(result.books).containsExactly(
            AladinAPIJsonNodeContext(createBookJsonNode(isbn = "isbn0000", publishDate = betweenThemRequestDate), publisherMapper),
            AladinAPIJsonNodeContext(createBookJsonNode(isbn = "isbn0001", publishDate = betweenThemRequestDate), publisherMapper),
            AladinAPIJsonNodeContext(createBookJsonNode(isbn = "isbn0002", publishDate = betweenThemRequestDate), publisherMapper)
        )
    }
}