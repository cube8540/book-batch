package cube8540.book.batch.external.nl.go

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.node.TextNode
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.errorCode
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.errorMessage
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.errorResult
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NationalLibraryAPIErrorDeserializerTest {

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