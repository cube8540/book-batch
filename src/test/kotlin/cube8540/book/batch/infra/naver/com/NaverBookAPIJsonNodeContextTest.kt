package cube8540.book.batch.infra.naver.com

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import cube8540.book.batch.domain.PublisherRawMapper
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NaverBookAPIJsonNodeContextTest {
    private val isbn0 = "9791136202093"
    private val responseIsbn0 = "1136215301 $isbn0"

    private val publisher = "publisher0000"
    private val publisherCode = "publisherCode0001"

    private val jsonNode: JsonNode = mockk(relaxed = true)
    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    private val context = NaverBookAPIJsonNodeContext(jsonNode, publisherRawMapper)

    @Test
    fun `resolve isbn`() {
        every { jsonNode.get(NaverBookAPIResponseNames.isbn) } returns TextNode(responseIsbn0)

        val result = context.resolveIsbn()
        assertThat(result).isEqualTo(isbn0)
    }

    @Test
    fun `resolve publisher`() {
        every { jsonNode.get(NaverBookAPIResponseNames.publisher) } returns TextNode(publisher)
        every { publisherRawMapper.mapping(publisher) } returns publisherCode

        val result = context.resolvePublisher()
        assertThat(result).isEqualTo(publisherCode)
    }

}