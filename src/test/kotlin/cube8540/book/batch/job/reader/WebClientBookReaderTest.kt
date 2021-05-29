package cube8540.book.batch.job.reader

import cube8540.book.batch.domain.BookDetailsContext
import cube8540.book.batch.external.BookAPIRequest
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.ExternalBookAPIExchanger
import cube8540.book.batch.job.BookAPIRequestJobParameter
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class WebClientBookReaderTest {

    private val publisher = "publisher00001"
    private val isbn = "isbn-00-000"
    private val from = LocalDate.of(2021, 5, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val exchanger: ExternalBookAPIExchanger = mockk(relaxed = true)
    private val request = BookAPIRequestJobParameter()

    private val reader = WebClientBookReader(exchanger, request)

    init {
        request.isbn = isbn
        request.publisher = publisher
        request.from = from
        request.to = to
    }

    @Test
    fun `read value api result is null`() {
        val request = BookAPIRequest(reader.page + 1, reader.pageSize, from, to, isbn, publisher)

        every { exchanger.exchange(request) } returns null

        val result = reader.read()
        assertThat(result).isNull()
    }

    @Test
    fun `read value api result books is empty`() {
        val request = BookAPIRequest(reader.page + 1, reader.pageSize, from, to, isbn, publisher)
        val bookAPIResponse: BookAPIResponse = mockk(relaxed = true)

        every { bookAPIResponse.books } returns emptyList()
        every { exchanger.exchange(request) } returns bookAPIResponse

        val result = reader.read()
        assertThat(result).isNull()
    }

    @Test
    fun `read value`() {
        val request = BookAPIRequest(reader.page + 1, reader.pageSize, from, to, isbn, publisher)
        val bookAPIResponse: BookAPIResponse = mockk(relaxed = true)
        val books: List<BookDetailsContext> = listOf(mockk(), mockk(), mockk())

        every { bookAPIResponse.books } returns books
        every { exchanger.exchange(request) } returns bookAPIResponse

        val result = reader.read()
        assertThat(result).isEqualTo(books.first())
    }
}