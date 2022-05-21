package cube8540.book.batch.external.aladin.kr

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.translator.BookAPIResponse

class AladinAPIDeserializer(private val publisherRawMapper: PublisherRawMapper)
    : StdDeserializer<BookAPIResponse>(BookAPIResponse::class.java) {
    override fun deserialize(p0: JsonParser, p1: DeserializationContext): BookAPIResponse {
        val responseNode = p0.codec.readTree<JsonNode>(p0)

        val total = responseNode.get(AladinAPIResponseNames.totalCount).asLong()
        val page = responseNode.get(AladinAPIResponseNames.page).asLong()

        return when (val bookNode = responseNode.get(AladinAPIResponseNames.items)) {
            null -> BookAPIResponse(total, page, emptyList())
            is ArrayNode -> BookAPIResponse(total, page, bookNode.map { AladinAPIJsonNodeContext(it, publisherRawMapper) })
            else -> BookAPIResponse(total, page, listOf(AladinAPIJsonNodeContext(bookNode, publisherRawMapper)))
        }
    }
}