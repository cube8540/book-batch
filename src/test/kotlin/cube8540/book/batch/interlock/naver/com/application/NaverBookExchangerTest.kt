package cube8540.book.batch.interlock.naver.com.application

import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.book.domain.defaultIsbn
import cube8540.book.batch.book.domain.defaultPublisher
import cube8540.book.batch.interlock.BookAPIRequest
import cube8540.book.batch.interlock.PageDecision
import cube8540.book.batch.interlock.naver.com.client.NaverBookClient
import cube8540.book.batch.interlock.naver.com.client.NaverBookClientRequest
import cube8540.book.batch.interlock.naver.com.client.NaverBookClientResponse
import cube8540.book.batch.interlock.naver.com.client.createBook
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

internal class NaverBookExchangerTest {

    private val randomPage = Random.nextInt()
    private val randomSize = Random.nextInt()

    private val from = LocalDate.of(2022, 4, 1)
    private val to = LocalDate.of(2022, 4, 30)

    private val client: NaverBookClient = mockk(relaxed = true)
    private val pageDecision: PageDecision = mockk(relaxed = true)
    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    private val exchanger: NaverBookExchanger = NaverBookExchanger(client, pageDecision, publisherRawMapper)

    @BeforeEach
    fun setup() {
        exchanger.maxRequestPage = NaverBookExchanger.DEFAULT_MAXIMUM_REQUEST_PAGE
    }

    @Test
    fun `return empty list when request page is grater then max page`() {
        val maxPage = 10000
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher, isbn = defaultIsbn)

        exchanger.maxRequestPage = maxPage
        every { pageDecision.calculation(randomPage, randomSize) } returns maxPage + 1

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result.totalCount).isZero
        assertThat(result.page).isZero
        assertThat(result.books).isEmpty()
    }

    @Test
    fun `exchange by book api`() {
        val requestedPage = NaverBookExchanger.DEFAULT_MAXIMUM_REQUEST_PAGE - 1
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher, isbn = defaultIsbn)

        val randomTotal = Random.nextInt()
        val formattedFrom = from.format(DateTimeFormatter.BASIC_ISO_DATE)
        val formattedTo = to.format(DateTimeFormatter.BASIC_ISO_DATE)
        val expectedClientRequest = NaverBookClientRequest(start = requestedPage, display = randomSize,
            from = formattedFrom, to = formattedTo, publisher = defaultPublisher, isbn = defaultIsbn
        )
        val clientResponse = NaverBookClientResponse(total = randomTotal, start = requestedPage, display = randomSize,
            items = listOf(createBook(isbn = "isbn000001"), createBook(isbn = "isbn000002"), createBook(isbn = "isbn000003"))
        )

        every { pageDecision.calculation(randomPage, randomSize) } returns requestedPage
        every { client.search(expectedClientRequest) } returns clientResponse

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result.page).isEqualTo(requestedPage.toLong())
        assertThat(result.totalCount).isEqualTo(randomTotal.toLong())
        assertThat(result.books).containsExactly(
            NaverBookResponseContext(createBook(isbn = "isbn000001"), publisherRawMapper),
            NaverBookResponseContext(createBook(isbn = "isbn000002"), publisherRawMapper),
            NaverBookResponseContext(createBook(isbn = "isbn000003"), publisherRawMapper)
        )
    }

}