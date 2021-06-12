package cube8540.book.batch.external.nl.go

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import cube8540.book.batch.external.BookAPIErrorResponse
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
        val responseNode = createNationalLibraryAPIErrorResponse(errorCode = "errorCode0000", errorMessage = "errorMessage00000")

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response).isEqualTo(BookAPIErrorResponse("errorCode0000", "errorMessage00000"))
    }

}