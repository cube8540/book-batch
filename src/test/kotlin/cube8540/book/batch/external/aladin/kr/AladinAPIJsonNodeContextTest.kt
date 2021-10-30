package cube8540.book.batch.external.aladin.kr

import cube8540.book.batch.book.domain.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AladinAPIJsonNodeContextTest {
    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    @Test
    fun `resolve authors empty string`() {
        val authors = emptySet<String>()

        val jsonNode = createBookJsonNode(author = authors)
        val context = AladinAPIJsonNodeContext(jsonNode, publisherRawMapper)

        val result = context.resolveAuthors()
        assertThat(result).isNull()
    }

    @Test
    fun `resolve authors`() {
        val authors = setOf("author0", "author1", "author2")

        val jsonNode = createBookJsonNode(author = authors)
        val context = AladinAPIJsonNodeContext(jsonNode, publisherRawMapper)

        val result = context.resolveAuthors()
        assertThat(result).isEqualTo(authors)
    }

    @Test
    fun `resolve publisher`() {
        val jsonNode = createBookJsonNode(publisher = "publisherCode")
        val context = AladinAPIJsonNodeContext(jsonNode, publisherRawMapper)

        every { publisherRawMapper.mapping("publisherCode") } returns "resolvedPublisherCode"

        val result = context.resolvePublisher()
        assertThat(result).isEqualTo("resolvedPublisherCode")
    }

    @Test
    fun `resolve external link`() {
        val jsonNode = createBookJsonNode(link = defaultLink, originalPrice = defaultOriginalPrice, salePrice = defaultSalePrice)
        val context = AladinAPIJsonNodeContext(jsonNode, publisherRawMapper)

        val result = context.resolveExternalLink()
        assertThat(result.keys).containsExactly(MappingType.ALADIN)
        assertThat(result[MappingType.ALADIN]!!.productDetailPage).isEqualTo(defaultLinkUri)
        assertThat(result[MappingType.ALADIN]!!.originalPrice).isEqualTo(defaultOriginalPrice)
        assertThat(result[MappingType.ALADIN]!!.salePrice).isEqualTo(defaultSalePrice)
    }
}