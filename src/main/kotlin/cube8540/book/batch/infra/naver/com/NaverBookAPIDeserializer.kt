package cube8540.book.batch.infra.naver.com

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import cube8540.book.batch.domain.BookDetailsContext
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIResponse

class NaverBookAPIDeserializer(private val publisherRawMapper: PublisherRawMapper)
    : StdDeserializer<BookAPIResponse>(BookAPIResponse::class.java) {

    override fun deserialize(p0: JsonParser, p1: DeserializationContext?): BookAPIResponse {
        val responseBody = p0.codec.readTree<JsonNode>(p0)

        val books: List<BookDetailsContext> = when (val bookNode = responseBody.get(NaverBookAPIResponseNames.item)) {
            null -> emptyList()
            is ArrayNode -> bookNode.map { NaverBookAPIJsonNodeContext(it, publisherRawMapper) }
            else -> listOf(NaverBookAPIJsonNodeContext(bookNode, publisherRawMapper))
        }

        val totalCount = responseBody.get(NaverBookAPIResponseNames.totalCount).asLong()
        val page = responseBody.get(NaverBookAPIResponseNames.start).asLong()
        return BookAPIResponse(totalCount, page, books)
    }
}