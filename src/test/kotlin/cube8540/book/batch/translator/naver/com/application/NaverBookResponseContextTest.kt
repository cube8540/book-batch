package cube8540.book.batch.translator.naver.com.application

import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.book.domain.defaultIsbn
import cube8540.book.batch.translator.naver.com.client.createBook
import cube8540.book.batch.translator.naver.com.client.defaultNaverBookAPIResponseIsbn
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class NaverBookResponseContextTest {

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    @Test
    fun `extract isbn when null`() {
        val book = createBook(isbn = null)
        val context = NaverBookResponseContext(book, publisherRawMapper)

        val result = context.extractIsbn()
        assertThat(result).isEmpty()
    }

    @Test
    fun `extract isbn when not contains empty space`() {
        val book = createBook(isbn = defaultIsbn)
        val context = NaverBookResponseContext(book, publisherRawMapper)

        val result = context.extractIsbn()
        assertThat(result).isEqualTo(defaultIsbn)
    }

    @Test
    fun `extract isbn`() {
        val book = createBook(isbn = defaultNaverBookAPIResponseIsbn)
        val context = NaverBookResponseContext(book, publisherRawMapper)

        val result = context.extractIsbn()
        assertThat(result).isEqualTo(defaultIsbn)
    }

    @Test
    fun `extract publisher`() {
        val book = createBook(publisher = "publisherCode")
        val context = NaverBookResponseContext(book, publisherRawMapper)

        every { publisherRawMapper.mapping("publisherCode") } returns "extractedPublisherCode"

        val result = context.extractPublisher()
        assertThat(result).isEqualTo("extractedPublisherCode")
    }
}