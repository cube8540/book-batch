package cube8540.book.batch.translator.aladin.kr.application

import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.book.domain.defaultPublisher
import cube8540.book.batch.translator.BookAPIRequest
import cube8540.book.batch.translator.BookAPIResponse
import cube8540.book.batch.translator.PageDecision
import cube8540.book.batch.translator.aladin.kr.client.*
import cube8540.book.batch.translator.naver.com.client.AladinAuthenticationInfo
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.random.Random

internal class AladinBookExchangerTest {

    private val randomPage = Random.nextInt(1, 4)
    private val randomSize = Random.nextInt(1, MAX_RESULTS)

    private val realRequestedPage = Random.nextInt(1, 999)

    private val from = LocalDate.of(2021, 5, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)
    private val pageDecision: PageDecision = mockk(relaxed = true)

    private val client: AladinBookClient = mockk(relaxed = true)
    private val key: AladinAuthenticationInfo = AladinAuthenticationInfo(defaultTtbKey)

    private val exchanger = AladinBookExchanger(client, key, publisherRawMapper, pageDecision)

    @Test
    fun `exchange book api when page multiple size is grater then maximum data count`() {
        val page = (MAX_REQUEST_DATA_COUNT / randomSize) + 1
        val exchangeParameter = BookAPIRequest(page = page, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        val result = exchanger.exchange(exchangeParameter)
        assertEmptyResponse(result)
    }

    @Test
    fun `exchange book api when size grater then maximum size`() {
        val exchangeParameter = BookAPIRequest(page = randomPage, size = MAX_RESULTS + 1, from = from, to = to, publisher = defaultPublisher)

        val result = exchanger.exchange(exchangeParameter)
        assertEmptyResponse(result)
    }

    @Test
    fun `exchange book data`() {
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        val randomTotal = Random.nextInt()
        val expectedClientRequest = AladinBookRequest(ttbKey = defaultTtbKey, query = defaultPublisher, start = realRequestedPage, maxResults = randomSize)
        val clientResponse = AladinBookClientResponse(totalResults = randomTotal, startIndex = realRequestedPage, itemsPerPage = randomSize, items = listOf(
            createBook(isbn = "isbn0000001", publishDate = from),
            createBook(isbn = "isbn0000002", publishDate = from),
            createBook(isbn = "isbn0000003", publishDate = from)
        ))

        every { pageDecision.calculation(randomPage, randomSize) } returns realRequestedPage
        every { client.search(expectedClientRequest) } returns clientResponse

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result.page).isEqualTo(realRequestedPage.toLong())
        assertThat(result.totalCount).isEqualTo(randomTotal.toLong())
        assertThat(result.books).containsExactly(
            AladinBookResponseContext(createBook(isbn = "isbn0000001", publishDate = from), publisherRawMapper),
            AladinBookResponseContext(createBook(isbn = "isbn0000002", publishDate = from), publisherRawMapper),
            AladinBookResponseContext(createBook(isbn = "isbn0000003", publishDate = from), publisherRawMapper)
        )
    }

    @Test
    fun `book api result contains not published on requested date`() {
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        val betweenRequestDate = from.plusDays(Random.nextLong(0, ChronoUnit.DAYS.between(from, to)))
        val beforeRequestDate = from.minusDays(Random.nextLong(1, 100))
        val afterRequestDate = to.plusDays(Random.nextLong(1, 100))

        val clientResponse = AladinBookClientResponse(items = listOf(
            createBook(isbn = "isbn0000001", publishDate = betweenRequestDate),
            createBook(isbn = "isbn0000002", publishDate = beforeRequestDate),
            createBook(isbn = "isbn0000003", publishDate = afterRequestDate)
        ))

        every { pageDecision.calculation(any(), any()) } returns realRequestedPage
        every { client.search(any()) } returns clientResponse

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result.books).containsExactly(
            AladinBookResponseContext(createBook(isbn = "isbn0000001", publishDate = betweenRequestDate), publisherRawMapper)
        )
    }

    private fun assertEmptyResponse(result: BookAPIResponse) {
        assertThat(result.page).isZero
        assertThat(result.totalCount).isZero
        assertThat(result.books).isEmpty()
    }
}