package cube8540.book.batch.translator.nl.go.application

import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.book.domain.defaultIsbn
import cube8540.book.batch.book.domain.defaultPublisher
import cube8540.book.batch.translator.BookAPIRequest
import cube8540.book.batch.translator.PageDecision
import cube8540.book.batch.translator.aladin.kr.client.MAX_RESULTS
import cube8540.book.batch.translator.client.ClientExchangeException
import cube8540.book.batch.translator.client.ErrorCodeExternalExceptionCreator
import cube8540.book.batch.translator.nl.go.*
import cube8540.book.batch.translator.nl.go.client.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

internal class NationalLibraryExchangerTest {

    private val randomPage = Random.nextInt()
    private val randomSize = Random.nextInt()
    private val realRequestedPage = Random.nextInt(1, MAX_RESULTS)

    private val from = LocalDate.of(2021,  5, 1)
    private val to = LocalDate.of(2021, 5, 31)
    private val formatter = DateTimeFormatter.BASIC_ISO_DATE

    private val client: NationalLibraryClient = mockk(relaxed = true)
    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)
    private val key: NationalLibraryAPIKey = NationalLibraryAPIKey(defaultNationalLibraryClientKey)
    private val exceptionCreator: ErrorCodeExternalExceptionCreator = mockk(relaxed = true)
    private val pageDecision: PageDecision = mockk(relaxed = true)

    private val exchanger = NationalLibraryExchanger(client, publisherRawMapper, key, exceptionCreator, pageDecision)

    init {
        exchanger.formatter = formatter
    }

    @Test
    fun `exchange book api when request page grater then max size`() {
        val requestedPage = MAX_REQUEST_PAGE + 1
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        every { pageDecision.calculation(randomPage, randomSize) } returns requestedPage

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result.totalCount).isZero
        assertThat(result.page).isZero
        assertThat(result.books).isEmpty()
    }

    @Test
    fun `api returns error`() {
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher, isbn = defaultIsbn)

        val exception: ClientExchangeException = mockk(relaxed = true)
        val clientResponse = NationalLibraryClientResponse(
            result = defaultErrorResult, errorCode = defaultErrorCode, errorMessage = defaultErrorMessage
        )

        every { client.search(any()) } returns clientResponse
        every { pageDecision.calculation(randomPage, randomSize) } returns realRequestedPage
        every { exceptionCreator.create(defaultErrorCode, defaultErrorMessage) } returns exception

        assertThatThrownBy { exchanger.exchange(exchangeParameter) }.isEqualTo(exception)
    }

    @Test
    fun `exchange book api`() {
        val exchangeParameter = BookAPIRequest(isbn = defaultIsbn, page = randomPage, size = randomSize, from = from, to = to, publisher = defaultPublisher)

        val randomTotal = Random.nextInt()
        val expectedClientRequest = NationalLibraryBookRequest(
            isbn = defaultIsbn,
            secretKey = defaultNationalLibraryClientKey,
            startPublishDate = "20210501", endPublishDate = "20210531",
            pageNo = realRequestedPage, pageSize = randomSize,
            publisher = defaultPublisher
        )
        val clientResponse = NationalLibraryClientResponse(totalCount = randomTotal, pageNo = realRequestedPage, docs = listOf(
            createBook(isbn = "isbn000000001"), createBook(isbn = "isbn000000002"), createBook(isbn = "isbn000000003")
        ))

        every { pageDecision.calculation(randomPage, randomSize) } returns realRequestedPage
        every { client.search(expectedClientRequest) } returns clientResponse

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result.totalCount).isEqualTo(randomTotal.toLong())
        assertThat(result.page).isEqualTo(realRequestedPage.toLong())
        assertThat(result.books).containsExactly(
            NationalLibraryBookContext(createBook(isbn = "isbn000000001"), publisherRawMapper),
            NationalLibraryBookContext(createBook(isbn = "isbn000000002"), publisherRawMapper),
            NationalLibraryBookContext(createBook(isbn = "isbn000000003"), publisherRawMapper)
        )
    }
}