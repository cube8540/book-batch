package cube8540.book.batch.external.naver.com

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIResponse
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NaverBookAPIDeserializerTest {

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    private val deserializer = NaverBookAPIDeserializer(publisherRawMapper)

    @Test
    fun `response deserialization when book is empty`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: ObjectMapper = mockk(relaxed = true)
        val responseNode = createNaverBookAPIResponse(items = null)

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.books).isEmpty()
    }

    @Test
    fun `response deserialization when book node is not array node`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: ObjectMapper = mockk(relaxed = true)
        val responseNode = createNaverBookAPIResponse(items = createBookJsonNode())

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.books.size).isEqualTo(1)
        assertThat(response.books.first()).isEqualTo(NaverBookAPIJsonNodeContext(createBookJsonNode(), publisherRawMapper))
    }

    @Test
    fun `response deserialization when book node is array node`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: ObjectMapper = mockk(relaxed = true)
        val bookArrayNode = createBookJsonArrayNode(
            createBookJsonNode(isbn = "isbn00000"),
            createBookJsonNode(isbn = "isbn00001"),
            createBookJsonNode(isbn = "isbn00002")
        )
        val responseNode: JsonNode = createNaverBookAPIResponse(total = 3, display = 3, start = 1, items = bookArrayNode)

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.books.size).isEqualTo(bookArrayNode.size())
        assertThat(response.books).containsExactly(
            NaverBookAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00000"), publisherRawMapper),
            NaverBookAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00001"), publisherRawMapper),
            NaverBookAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00002"), publisherRawMapper)
        )
    }
}