package cube8540.book.batch.job.reader

import cube8540.book.batch.book.domain.createBookContext
import cube8540.book.batch.book.domain.defaultIsbn
import cube8540.book.batch.book.domain.defaultPublisherCode
import cube8540.book.batch.interlock.BookAPIRequest
import cube8540.book.batch.interlock.BookAPIResponse
import cube8540.book.batch.interlock.ExternalBookAPIExchanger
import cube8540.book.batch.job.BookAPIRequestJobParameter
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class WebClientBookReaderTest {

    private val from = LocalDate.of(2021, 5, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val exchanger: ExternalBookAPIExchanger = mockk(relaxed = true)
    private val request = BookAPIRequestJobParameter()

    private lateinit var reader: WebClientBookReader

    @BeforeEach
    fun setup() {
        request.isbn = defaultIsbn
        request.publisher = defaultPublisherCode
        request.from = from
        request.to = to

        reader = WebClientBookReader(exchanger, request)
    }

    @Test
    fun `read value api result is null`() {
        val request = BookAPIRequest(reader.page + 1, reader.pageSize, from, to, defaultIsbn, defaultPublisherCode)

        every { exchanger.exchange(request) } returns null

        val result = reader.read()
        assertThat(result).isNull()
    }

    @Test
    fun `read value api result books is empty`() {
        val request = BookAPIRequest(reader.page + 1, reader.pageSize, from, to, defaultIsbn, defaultPublisherCode)
        val bookAPIResponse = BookAPIResponse()

        every { exchanger.exchange(request) } returns bookAPIResponse

        val result = reader.read()
        assertThat(result).isNull()
    }

    @Test
    fun `read value`() {
        val request = BookAPIRequest(reader.page + 1, reader.pageSize, from, to, defaultIsbn, defaultPublisherCode)
        val bookDetailsContext = listOf(
            createBookContext(isbn = "isbn0000"),
            createBookContext(isbn = "isbn0001"),
            createBookContext(isbn = "isbn0002")
        )
        val bookAPIResponse = BookAPIResponse(totalCount = 3, page = reader.page.toLong(), books = bookDetailsContext)

        every { exchanger.exchange(request) } returns bookAPIResponse

        val result = reader.read()
        assertThat(result).isEqualTo(bookDetailsContext.first())
    }
}