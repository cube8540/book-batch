package cube8540.book.batch.infra.naver.com

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import cube8540.book.batch.external.BookAPIErrorResponse

class NaverBookAPIErrorDeserializer: StdDeserializer<BookAPIErrorResponse>(BookAPIErrorResponse::class.java) {
    override fun deserialize(p0: JsonParser, p1: DeserializationContext?): BookAPIErrorResponse {
        val resultNode = p0.codec.readTree<JsonNode>(p0)

        return BookAPIErrorResponse(
            resultNode.get(NaverBookAPIResponseNames.errorCode).asText().trim(),
            resultNode.get(NaverBookAPIResponseNames.errorMessage).asText().trim()
        )
    }
}