package cube8540.book.batch.infra.naver.com

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIResponse
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NaverBookAPIDeserializerTest {

    private val totalCount = 100
    private val display = 10
    private val start = 1

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    private val deserializer = NaverBookAPIDeserializer(publisherRawMapper)

    @Test
    fun `response deserialization when book is empty`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: XmlMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)

        every { responseNode.get(NaverBookAPIResponseNames.totalCount) } returns IntNode(totalCount)
        every { responseNode.get(NaverBookAPIResponseNames.start) } returns IntNode(start)
        every { responseNode.get(NaverBookAPIResponseNames.display) } returns IntNode(display)
        every { responseNode.get(NaverBookAPIResponseNames.item) } returns null

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.page).isEqualTo(start.toLong())
        assertThat(response.totalCount).isEqualTo(totalCount.toLong())
        assertThat(response.books).isEqualTo(emptyList<BookDetails>())
    }

    @Test
    fun `response deserialization when book node is not array node`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: XmlMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)
        val bookNode: JsonNode = mockk(relaxed = true)

        every { responseNode.get(NaverBookAPIResponseNames.start) } returns IntNode(start)
        every { responseNode.get(NaverBookAPIResponseNames.totalCount) } returns IntNode(totalCount)
        every { responseNode.get(NaverBookAPIResponseNames.display) } returns IntNode(display)
        every { responseNode.get(NaverBookAPIResponseNames.item) } returns bookNode


        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.page).isEqualTo(start.toLong())
        assertThat(response.totalCount).isEqualTo(totalCount.toLong())
        assertThat(response.books.size).isEqualTo(1)
        assertThat(response.books.first()).isEqualTo(NaverBookAPIJsonNodeContext(bookNode, publisherRawMapper))
    }

    @Test
    fun `response deserialization when book node is array node`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: XmlMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)
        val bookNode0: JsonNode = mockk(relaxed = true)
        val bookNode1: JsonNode = mockk(relaxed = true)
        val bookNode2: JsonNode = mockk(relaxed = true)
        val bookArrayNode = ArrayNode(mockk(relaxed = true))

        bookArrayNode.add(bookNode0)
        bookArrayNode.add(bookNode1)
        bookArrayNode.add(bookNode2)

        every { responseNode.get(NaverBookAPIResponseNames.start) } returns IntNode(start)
        every { responseNode.get(NaverBookAPIResponseNames.totalCount) } returns IntNode(totalCount)
        every { responseNode.get(NaverBookAPIResponseNames.display) } returns IntNode(display)
        every { responseNode.get(NaverBookAPIResponseNames.item) } returns bookArrayNode

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.page).isEqualTo(start.toLong())
        assertThat(response.totalCount).isEqualTo(totalCount.toLong())
        assertThat(response.books.size).isEqualTo(3)
        assertThat(response.books[0]).isEqualTo(NaverBookAPIJsonNodeContext(bookNode0, publisherRawMapper))
        assertThat(response.books[1]).isEqualTo(NaverBookAPIJsonNodeContext(bookNode1, publisherRawMapper))
        assertThat(response.books[2]).isEqualTo(NaverBookAPIJsonNodeContext(bookNode2, publisherRawMapper))
    }
}