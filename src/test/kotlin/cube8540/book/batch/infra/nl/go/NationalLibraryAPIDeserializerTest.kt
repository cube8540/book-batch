package cube8540.book.batch.infra.nl.go

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.TextNode
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.exception.InternalBadRequestException
import cube8540.book.batch.infra.nl.go.NationalLibraryAPIDeserializerTestEnvironment.pageNumber
import cube8540.book.batch.infra.nl.go.NationalLibraryAPIDeserializerTestEnvironment.totalCount
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test

class NationalLibraryAPIDeserializerTest {
    private val errorResult = "ERROR"
    private val errorCode = "errorCode0001"
    private val errorMessage = "errorMessage0001"

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    private val deserializer = NationalLibraryAPIDeserializer(publisherRawMapper)

    @Test
    fun `response is error`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)

        deserializer.exceptionCreator = mockk(relaxed = true) {
            every { create(errorCode, errorMessage) } returns InternalBadRequestException(errorMessage)
        }

        every { responseNode.get(NationalLibraryAPIResponseNames.result) } returns TextNode(errorResult)
        every { responseNode.get(NationalLibraryAPIResponseNames.errorCode) } returns TextNode(errorCode)
        every { responseNode.get(NationalLibraryAPIResponseNames.errorMessage) } returns TextNode(errorMessage)

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val thrown = catchThrowable { deserializer.deserialize(jsonParser, mockk(relaxed = true)) }
        assertThat(thrown).isInstanceOf(InternalBadRequestException::class.java)
        assertThat((thrown as InternalBadRequestException).message).isEqualTo(errorMessage)
    }

    @Test
    fun `response deserialization when book is empty`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)

        every { responseNode.get(NationalLibraryAPIResponseNames.pageNo) } returns TextNode(pageNumber.toString())
        every { responseNode.get(NationalLibraryAPIResponseNames.totalCount) } returns TextNode(totalCount.toString())
        every { responseNode.get(NationalLibraryAPIResponseNames.documents) } returns null

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.page).isEqualTo(pageNumber.toLong())
        assertThat(response.totalCount).isEqualTo(totalCount.toLong())
        assertThat(response.books).isEqualTo(emptyList<BookDetails>())
    }

    @Test
    fun `response deserialization when book is not empty`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)

        val documentsNode = ArrayNode(mockk(relaxed = true))
        val bookNode: JsonNode = mockk(relaxed = true)

        documentsNode.add(bookNode)

        every { responseNode.get(NationalLibraryAPIResponseNames.pageNo) } returns TextNode(pageNumber.toString())
        every { responseNode.get(NationalLibraryAPIResponseNames.totalCount) } returns TextNode(totalCount.toString())
        every { responseNode.get(NationalLibraryAPIResponseNames.documents) } returns documentsNode

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.books[0]).isEqualTo(NationalLibraryJsonNodeContext(bookNode, publisherRawMapper))
    }
}
