package cube8540.book.batch.translator.aladin.kr.application

import cube8540.book.batch.book.domain.*
import cube8540.book.batch.translator.aladin.kr.client.createBook
import cube8540.book.batch.translator.aladin.kr.client.defaultIsbn10
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AladinBookResponseContextTest {

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    @Test
    fun `extract isbn`() {
        val book = createBook(isbn = defaultIsbn, isbn10 = defaultIsbn10)
        val context = AladinBookResponseContext(book, publisherRawMapper)

        val result = context.extractIsbn()
        assertThat(result).isEqualTo(defaultIsbn)
    }

    @Test
    fun `extract authors`() {
        val authors = setOf("author0", "author1", "author2")

        val book = createBook(author = authors.joinToString(", "))
        val context = AladinBookResponseContext(book, publisherRawMapper)

        val result = context.extractAuthors()
        assertThat(result).containsExactly("author0", "author1", "author2")
    }

    @Test
    fun `extract publisher`() {
        val book = createBook(publisher = "publisherCode")
        val context = AladinBookResponseContext(book, publisherRawMapper)

        every { publisherRawMapper.mapping("publisherCode") } returns "extractedPublisherCode"

        val result = context.extractPublisher()
        assertThat(result).isEqualTo("extractedPublisherCode")
    }

    @Test
    fun `extract external link`() {
        val book = createBook(link = defaultLink, originalPrice = defaultOriginalPrice, salePrice = defaultSalePrice)
        val context = AladinBookResponseContext(book, publisherRawMapper)

        val result = context.extractExternalLink()!!
        assertThat(result).containsOnlyKeys(MappingType.ALADIN)
        assertThat(result[MappingType.ALADIN]).isEqualTo(BookExternalLink(defaultLinkUri, defaultOriginalPrice, defaultSalePrice))
    }
}