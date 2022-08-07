package cube8540.book.batch.translator.kyobo.kr.application

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController
import cube8540.book.batch.book.domain.createBookContext
import cube8540.book.batch.book.domain.createBookDetails
import cube8540.book.batch.translator.kyobo.kr.createDocument
import cube8540.book.batch.translator.kyobo.kr.client.KyoboBookClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Test

class KyoboClientBookProcessorTest {

    private val client: KyoboBookClient = mockk(relaxed = true)
    private val bookDocumentMapper: BookDocumentMapper = mockk(relaxed = true)

    private val controller: BookDetailsController = mockk(relaxed = true)

    private val processor = KyoboClientBookProcessor(client, bookDocumentMapper)

    init {
        processor.controller = controller
    }

    @Test
    fun `book exchange and merge`() {
        val exchangeHtml = createDocument().html()

        val bookContext = createBookContext(isbn = "extractIsbn")
        val bookDetails = createBookDetails(isbn = "originalIsbn")
        val mergeCompletedResult: BookDetails = mockk(relaxed = true)

        val documentCaptor = slot<Document>()
        val mergedBookCaptor = slot<BookDetails>()
        val expectedDocumentHtml = Jsoup.parse(exchangeHtml)
            .outputSettings(Document.OutputSettings().prettyPrint(false))

        every { client.search("originalIsbn") } returns exchangeHtml
        every { bookDocumentMapper.convertValue(capture(documentCaptor)) } returns bookContext
        every { controller.merge(bookDetails, capture(mergedBookCaptor)) } returns mergeCompletedResult

        val result = processor.process(bookDetails)
        assertThat(result).isEqualTo(mergeCompletedResult)
        assertThat(documentCaptor.captured.html()).isEqualTo(expectedDocumentHtml.html())
        assertThat(mergedBookCaptor.captured).isEqualTo(createBookDetails(isbn = "extractIsbn"))
    }
}