package cube8540.book.batch.job.writer

import cube8540.book.batch.book.domain.createBookDetails
import cube8540.book.batch.external.BookUpstreamAPIRequest
import cube8540.book.batch.external.ExternalBookAPIUpstream
import cube8540.book.batch.job.createBookUpstreamRequest
import cube8540.book.batch.job.createBookUpstreamRequestDetails
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WebClientBookUpstreamWriterTest {

    private val upstream: ExternalBookAPIUpstream = mockk(relaxed = true)

    private val writer = WebClientBookUpstreamWriter(upstream)

    @Test
    fun `upstream book`() {
        val bookDetails = mutableListOf(
            createBookDetails(isbn = "isbn0000"),
            createBookDetails(isbn = "isbn0001"),
            createBookDetails(isbn = "isbn0002")
        )
        val bookUpstreamRequestCaptor = slot<BookUpstreamAPIRequest>()

        every { upstream.upstream(capture(bookUpstreamRequestCaptor)) } just runs

        writer.write(bookDetails)
        assertThat(bookUpstreamRequestCaptor.captured).isEqualTo(createBookUpstreamRequest(
            createBookUpstreamRequestDetails(isbn = "isbn0000"),
            createBookUpstreamRequestDetails(isbn = "isbn0001"),
            createBookUpstreamRequestDetails(isbn = "isbn0002")
        ))
    }
}