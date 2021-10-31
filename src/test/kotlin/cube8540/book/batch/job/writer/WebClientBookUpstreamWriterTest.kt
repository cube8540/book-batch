package cube8540.book.batch.job.writer

import cube8540.book.batch.book.application.BookCommandService
import cube8540.book.batch.book.domain.*
import cube8540.book.batch.external.BookUpstreamAPIRequest
import cube8540.book.batch.external.BookUpstreamExternalLink
import cube8540.book.batch.external.ExternalBookAPIUpstream
import cube8540.book.batch.job.createBookUpstreamRequest
import cube8540.book.batch.job.createBookUpstreamRequestDetails
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WebClientBookUpstreamWriterTest {

    private val upstream: ExternalBookAPIUpstream = mockk(relaxed = true)
    private val commandService: BookCommandService = mockk(relaxed = true)

    private val writer = WebClientBookUpstreamWriter(upstream, commandService)

    @Test
    fun `upstream book`() {
        val bookExternalLink = BookExternalLink(defaultLinkUri, defaultOriginalPrice, defaultSalePrice)
        val bookDetails = mutableListOf(
            createBookDetails(isbn = "isbn0000", externalLink = mapOf(MappingType.ALADIN to bookExternalLink)),
            createBookDetails(isbn = "isbn0001", externalLink = mapOf(MappingType.ALADIN to bookExternalLink)),
            createBookDetails(isbn = "isbn0002", externalLink = mapOf(MappingType.ALADIN to bookExternalLink))
        )
        val bookUpstreamRequestCaptor = slot<BookUpstreamAPIRequest>()
        val upstreamBookExternalLink = BookUpstreamExternalLink(defaultLink, defaultOriginalPrice, defaultSalePrice)

        every { upstream.upstream(capture(bookUpstreamRequestCaptor)) } just runs

        writer.write(bookDetails)
        assertThat(bookUpstreamRequestCaptor.captured).isEqualTo(createBookUpstreamRequest(
            createBookUpstreamRequestDetails(isbn = "isbn0000", upstreamExternalLink = mapOf(MappingType.ALADIN to upstreamBookExternalLink)),
            createBookUpstreamRequestDetails(isbn = "isbn0001", upstreamExternalLink = mapOf(MappingType.ALADIN to upstreamBookExternalLink)),
            createBookUpstreamRequestDetails(isbn = "isbn0002", upstreamExternalLink = mapOf(MappingType.ALADIN to upstreamBookExternalLink))
        ))
    }

    @Test
    fun `book set upstream false after api call`() {
        val bookDetails = mutableListOf(
            createBookDetails(isbn = "isbn0000", isUpstream = true),
            createBookDetails(isbn = "isbn0001", isUpstream = true),
            createBookDetails(isbn = "isbn0002", isUpstream = true)
        )
        val bookUpstreamRequestCaptor = slot<BookUpstreamAPIRequest>()
        val setUpstreamRequestCaptor = slot<List<BookDetails>>()

        writer.write(bookDetails)
        verifyOrder {
            upstream.upstream(capture(bookUpstreamRequestCaptor))
            commandService.updateForUpstream(capture(setUpstreamRequestCaptor))
        }
        assertThat(setUpstreamRequestCaptor.captured)
            .usingElementComparatorIgnoringFields(*bookDetailsAssertIgnoringFields)
            .containsExactly(
                createBookDetails(isbn = "isbn0000", isUpstream = false),
                createBookDetails(isbn = "isbn0001", isUpstream = false),
                createBookDetails(isbn = "isbn0002", isUpstream = false)
            )
    }
}