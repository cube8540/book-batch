package cube8540.book.batch.infra.naver.com

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import cube8540.book.batch.infra.naver.com.NaverBookAPIDeserializerTestEnvironment.errorCode
import cube8540.book.batch.infra.naver.com.NaverBookAPIDeserializerTestEnvironment.errorMessage
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NaverBookAPIErrorDeserializerTest {

    private val deserializer = NaverBookAPIErrorDeserializer()

    @Test
    fun `error deserialization`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: XmlMapper = mockk(relaxed = true)
        val resultNode: JsonNode = mockk(relaxed = true)
        val errorCodeNode: JsonNode = mockk(relaxed = true) {
            every { asText() } returns errorCode
        }
        val errorMessageNode: JsonNode = mockk(relaxed = true) {
            every { asText() } returns errorMessage
        }

        every { resultNode.get(NaverBookAPIResponseNames.errorCode) } returns errorCodeNode
        every { resultNode.get(NaverBookAPIResponseNames.errorMessage) } returns errorMessageNode

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns resultNode

        val response = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.code).isEqualTo(errorCode)
        assertThat(response.message).isEqualTo(errorMessage)
    }
}