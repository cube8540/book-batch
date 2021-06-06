package cube8540.book.batch.external.naver.com

import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.book.domain.defaultIsbn
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NaverBookAPIJsonNodeContextTest {
    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    @Test
    fun `resolve isbn`() {
        val jsonNode = createBookJsonNode(isbn = defaultNaverBookAPIResponseIsbn)
        val context = NaverBookAPIJsonNodeContext(jsonNode, publisherRawMapper)

        val result = context.resolveIsbn()
        assertThat(result).isEqualTo(defaultIsbn)
    }

    @Test
    fun `resolve isbn is null`() {
        val jsonNode = createBookJsonNode(isbn = null)
        val context = NaverBookAPIJsonNodeContext(jsonNode, publisherRawMapper)

        val result = context.resolveIsbn()
        assertThat(result).isEmpty()
    }

    @Test
    fun `resolve isbn is not has empty space`() {
        val jsonNode = createBookJsonNode(isbn = defaultIsbn)
        val context = NaverBookAPIJsonNodeContext(jsonNode, publisherRawMapper)

        val result = context.resolveIsbn()
        assertThat(result).isEmpty()
    }

    @Test
    fun `resolve publisher`() {
        val jsonNode = createBookJsonNode(publisher = "publisherCode")
        val context = NaverBookAPIJsonNodeContext(jsonNode, publisherRawMapper)

        every { publisherRawMapper.mapping("publisherCode") } returns "resolvedPublisherCode"

        val result = context.resolvePublisher()
        assertThat(result).isEqualTo("resolvedPublisherCode")
    }
}