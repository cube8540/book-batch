package cube8540.book.batch.external.aladin.kr

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.interlock.BookAPIResponse
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AladinAPIDeserializerTest {

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    private val deserializer = AladinAPIDeserializer(publisherRawMapper)

    @Test
    fun `response deserialization when book is empty`() {
        val jsonParse: JsonParser = mockk(relaxed = true)
        val codec: ObjectMapper = mockk(relaxed = true)
        val responseNode = createAladinBookAPIResponse(items = null)

        every { jsonParse.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParse) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParse, mockk(relaxed = true))
        assertThat(response.books).isEmpty()
    }

    @Test
    fun `response deserialization when book is not array node`() {
        val jsonParse: JsonParser = mockk(relaxed = true)
        val codec: ObjectMapper = mockk(relaxed = true)
        val bookNode = createBookJsonNode(isbn = "isbn00000")
        val responseNode: JsonNode = createAladinBookAPIResponse(total = 1, start = 1, items = bookNode)

        every { jsonParse.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParse) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParse, mockk(relaxed = true))
        assertThat(response.books.size).isEqualTo(1)
        assertThat(response.books.first())
            .isEqualTo(AladinAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00000"), publisherRawMapper))
    }

    @Test
    fun `response deserialization when book is array node`() {
        val jsonParse: JsonParser = mockk(relaxed = true)
        val codec: ObjectMapper = mockk(relaxed = true)
        val bookArrayNode = createBookJsonArrayNode(
            createBookJsonNode(isbn = "isbn00000"),
            createBookJsonNode(isbn = "isbn00001"),
            createBookJsonNode(isbn = "isbn00002")
        )
        val responseNode: JsonNode = createAladinBookAPIResponse(total = 3, start = 1, items = bookArrayNode)

        every { jsonParse.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParse) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParse, mockk(relaxed = true))
        assertThat(response.books.size).isEqualTo(bookArrayNode.size())
        assertThat(response.books).containsExactly(
            AladinAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00000"), publisherRawMapper),
            AladinAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00001"), publisherRawMapper),
            AladinAPIJsonNodeContext(createBookJsonNode(isbn = "isbn00002"), publisherRawMapper)
        )
    }

}