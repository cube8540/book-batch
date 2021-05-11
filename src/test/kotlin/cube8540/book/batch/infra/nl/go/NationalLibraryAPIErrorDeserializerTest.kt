package cube8540.book.batch.infra.nl.go

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.node.TextNode
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NationalLibraryAPIErrorDeserializerTest {

    private val errorResult = "ERROR"
    private val errorMessage = "errorMessage0001"
    private val errorCode = "errorCode0001"

    private val deserializer = NationalLibraryAPIErrorDeserializer()

    @Test
    fun `error deserialization`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)

        every { responseNode.get(NationalLibraryAPIResponseNames.result) } returns TextNode(errorResult)
        every { responseNode.get(NationalLibraryAPIResponseNames.errorCode) } returns TextNode(errorCode)
        every { responseNode.get(NationalLibraryAPIResponseNames.errorMessage) } returns TextNode(errorMessage)

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.code).isEqualTo(errorCode)
        assertThat(response.message).isEqualTo(errorMessage)
    }

}