package cube8540.book.batch.external.nl.go

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.exception.InternalBadRequestException
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class NationalLibraryAPIDeserializerTest {

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    private val deserializer = NationalLibraryAPIDeserializer(publisherRawMapper)

    @Test
    fun `response is error`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)
        val responseNode = createNationalLibraryAPIErrorResponse(errorCode = "errorCode0000", errorMessage = "errorMessage00000")

        deserializer.exceptionCreator = mockk(relaxed = true) {
            every { create("errorCode0000", "errorMessage00000") } returns InternalBadRequestException("errorMessage00000")
        }

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        assertThatThrownBy { deserializer.deserialize(jsonParser, mockk(relaxed = true)) }
            .isInstanceOf(InternalBadRequestException::class.java)
            .hasMessage("errorMessage00000")
    }

    @Test
    fun `response deserialization when book is empty`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)
        val responseNode = createNationalLibraryAPIResponse(page = 1, total = 0, docs = null)

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.page).isEqualTo(1L)
        assertThat(response.totalCount).isZero
        assertThat(response.books).isEqualTo(emptyList<BookDetails>())
    }

    @Test
    fun `response deserialization when book is not empty`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)

        val bookArray = createBookJsonArrayNode(
            createBookJsonNode(isbn = "isbn00000"),
            createBookJsonNode(isbn = "isbn00001"),
            createBookJsonNode(isbn = "isbn00002")
        )
        val bookResponse = createNationalLibraryAPIResponse(total = 3, page = 1, docs = bookArray)

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns bookResponse

        val response = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.books.size).isEqualTo(3)
        assertThat(response.books).contains(
            NationalLibraryJsonNodeContext(createBookJsonNode(isbn = "isbn00000"), publisherRawMapper),
            NationalLibraryJsonNodeContext(createBookJsonNode(isbn = "isbn00001"), publisherRawMapper),
            NationalLibraryJsonNodeContext(createBookJsonNode(isbn = "isbn00002"), publisherRawMapper)
        )
    }
}
