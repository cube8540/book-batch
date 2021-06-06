package cube8540.book.batch.external.naver.com

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.external.BookAPIErrorResponse
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NaverBookAPIErrorDeserializerTest {

    private val deserializer = NaverBookAPIErrorDeserializer()

    @Test
    fun `error deserialization`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: ObjectMapper = mockk(relaxed = true)
        val resultNode = createNaverBookErrorResponse(errorCode = "errorCode0000", errorMessage = "errorMessage0000")

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns resultNode

        val response = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response).isEqualTo(BookAPIErrorResponse("errorCode0000", "errorMessage0000"))
    }
}