package cube8540.book.batch.external.aladin.kr

import cube8540.book.batch.book.domain.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AladinAPIJsonNodeContextTest {
    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    @Test
    fun `extract authors empty string`() {
        val authors = emptySet<String>()

        val jsonNode = createBookJsonNode(author = authors)
        val context = AladinAPIJsonNodeContext(jsonNode, publisherRawMapper)

        val result = context.extractAuthors()
        assertThat(result).isNull()
    }

    @Test
    fun `extract authors`() {
        val authors = setOf("author0", "author1", "author2")

        val jsonNode = createBookJsonNode(author = authors)
        val context = AladinAPIJsonNodeContext(jsonNode, publisherRawMapper)

        val result = context.extractAuthors()
        assertThat(result).isEqualTo(authors)
    }

    @Test
    fun `extract publisher`() {
        val jsonNode = createBookJsonNode(publisher = "publisherCode")
        val context = AladinAPIJsonNodeContext(jsonNode, publisherRawMapper)

        every { publisherRawMapper.mapping("publisherCode") } returns "extractedPublisherCode"

        val result = context.extractPublisher()
        assertThat(result).isEqualTo("extractedPublisherCode")
    }

    @Test
    fun `extract external link`() {
        val jsonNode = createBookJsonNode(link = defaultLink, originalPrice = defaultOriginalPrice, salePrice = defaultSalePrice)
        val context = AladinAPIJsonNodeContext(jsonNode, publisherRawMapper)

        val result = context.extractExternalLink()
        assertThat(result.keys).containsExactly(MappingType.ALADIN)
        assertThat(result[MappingType.ALADIN]!!.productDetailPage).isEqualTo(defaultLinkUri)
        assertThat(result[MappingType.ALADIN]!!.originalPrice).isEqualTo(defaultOriginalPrice)
        assertThat(result[MappingType.ALADIN]!!.salePrice).isEqualTo(defaultSalePrice)
    }
}