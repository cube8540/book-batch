package cube8540.book.batch.external.naver.com

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.OriginalPropertyKey
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIResponse
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NaverBookAPIDeserializer(private val publisherRawMapper: PublisherRawMapper)
    : StdDeserializer<BookAPIResponse>(BookAPIResponse::class.java) {

    override fun deserialize(p0: JsonParser, p1: DeserializationContext?): BookAPIResponse {
        val channel = p0.codec.readTree<JsonNode>(p0)
        val responseBody = channel.get(NaverBookAPIResponseNames.channel)!!

        val books: List<BookDetails> = when (val bookNode = responseBody.get(NaverBookAPIResponseNames.item)) {
            null -> emptyList()
            is ArrayNode -> bookNode.map { singleBookDeserialization(it) }
            else -> listOf(singleBookDeserialization(bookNode))
        }

        val totalCount = responseBody.get(NaverBookAPIResponseNames.totalCount).asLong()
        val page = responseBody.get(NaverBookAPIResponseNames.start).asLong()
        return BookAPIResponse(totalCount, page, books)
    }

    private fun singleBookDeserialization(bookNode: JsonNode): BookDetails {
        val isbn = bookNode.get(NaverBookAPIResponseNames.isbn)!!
            .asText().split(" ")[1]

        val bookDetails = BookDetails(isbn)
        bookDetails.title = bookNode.get(NaverBookAPIResponseNames.title)?.asText()
        bookDetails.publisher = bookNode.get(NaverBookAPIResponseNames.publisher)
            ?.let { publisherRawMapper.mapping(it.textValue()) }
        bookDetails.price = bookNode.get(NaverBookAPIResponseNames.price)?.asDouble()
        bookDetails.publishDate = bookNode.get(NaverBookAPIResponseNames.publishDate)
            ?.let { LocalDate.parse(it.textValue(), DateTimeFormatter.BASIC_ISO_DATE) }
        bookDetails.smallThumbnail = bookNode.get(NaverBookAPIResponseNames.image)
            ?.let { URI.create(it.asText()) }

        val original = HashMap<OriginalPropertyKey, String?>()
        setOriginalNodeData(NaverBookAPIResponseNames.isbn, bookNode, original)
        setOriginalNodeData(NaverBookAPIResponseNames.title, bookNode, original)
        setOriginalNodeData(NaverBookAPIResponseNames.link, bookNode, original)
        setOriginalNodeData(NaverBookAPIResponseNames.image, bookNode, original)
        setOriginalNodeData(NaverBookAPIResponseNames.author, bookNode, original)
        setOriginalNodeData(NaverBookAPIResponseNames.price, bookNode, original)
        setOriginalNodeData(NaverBookAPIResponseNames.discount, bookNode, original)
        setOriginalNodeData(NaverBookAPIResponseNames.publisher, bookNode, original)
        setOriginalNodeData(NaverBookAPIResponseNames.publishDate, bookNode, original)
        setOriginalNodeData(NaverBookAPIResponseNames.description, bookNode, original)
        bookDetails.original = original

        return bookDetails
    }

    private fun setOriginalNodeData(fieldName: String, bookNode: JsonNode, map: MutableMap<OriginalPropertyKey, String?>) {
        map[OriginalPropertyKey(fieldName, MappingType.NAVER_BOOK)] = bookNode.get(fieldName)?.asText()
    }
}