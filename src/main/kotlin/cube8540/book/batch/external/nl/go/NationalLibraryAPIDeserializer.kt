package cube8540.book.batch.external.nl.go

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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NationalLibraryAPIDeserializer(private val publisherRawMapper: PublisherRawMapper)
    : StdDeserializer<BookAPIResponse>(BookAPIResponse::class.java) {

    override fun deserialize(p0: JsonParser, p1: DeserializationContext?): BookAPIResponse {
        val responseNode = p0.codec.readTree<JsonNode>(p0)

        val page = responseNode.get(NationalLibraryAPIResponseNames.pageNo).asText()
        val totalCount = responseNode.get(NationalLibraryAPIResponseNames.totalCount).asText()

        val bookDetails: List<BookDetails> = when (val documents = responseNode.get(NationalLibraryAPIResponseNames.documents)) {
            null -> emptyList()
            else -> (documents as ArrayNode).map { singleBookDeserialization(it) }
        }

        return BookAPIResponse(totalCount.toLong(), page.toLong(), bookDetails)
    }

    private fun singleBookDeserialization(bookNode: JsonNode): BookDetails {
        val isbn = bookNode.get(NationalLibraryAPIResponseNames.isbn)!!.asText()

        val bookDetails = BookDetails(isbn)
        bookDetails.title = bookNode.get(NationalLibraryAPIResponseNames.title).asText()
        bookDetails.publisher = bookNode.get(NationalLibraryAPIResponseNames.publisher)
            ?.let { publisherRawMapper.mapping(it.asText()) }

        val publishDate: String? = when (val publishDate = bookNode.get(NationalLibraryAPIResponseNames.realPublishDate)) {
            null -> bookNode.get(NationalLibraryAPIResponseNames.publishPreDate)?.asText()
            else -> {
                if (publishDate.textValue().isEmpty()) {
                    bookNode.get(NationalLibraryAPIResponseNames.publishPreDate).asText()
                } else {
                    publishDate.asText()
                }
            }
        }
        bookDetails.publishDate = publishDate?.let { LocalDate.parse(it, DateTimeFormatter.BASIC_ISO_DATE) }

        val original = HashMap<OriginalPropertyKey, String?>()
        setOriginalNodeData(NationalLibraryAPIResponseNames.isbn, bookNode, original)
        setOriginalNodeData(NationalLibraryAPIResponseNames.title, bookNode, original)
        setOriginalNodeData(NationalLibraryAPIResponseNames.publisher, bookNode, original)
        setOriginalNodeData(NationalLibraryAPIResponseNames.realPublishDate, bookNode, original)
        setOriginalNodeData(NationalLibraryAPIResponseNames.publishPreDate, bookNode, original)
        setOriginalNodeData(NationalLibraryAPIResponseNames.setIsbn, bookNode, original)
        setOriginalNodeData(NationalLibraryAPIResponseNames.additionalCode, bookNode, original)
        setOriginalNodeData(NationalLibraryAPIResponseNames.setAdditionalCode, bookNode, original)
        setOriginalNodeData(NationalLibraryAPIResponseNames.seriesNo, bookNode, original)
        setOriginalNodeData(NationalLibraryAPIResponseNames.setExpression, bookNode, original)
        setOriginalNodeData(NationalLibraryAPIResponseNames.subject, bookNode, original)
        setOriginalNodeData(NationalLibraryAPIResponseNames.author, bookNode, original)
        setOriginalNodeData(NationalLibraryAPIResponseNames.updateDate, bookNode, original)
        bookDetails.original = original
        return bookDetails
    }

    private fun setOriginalNodeData(fieldName: String, bookNode: JsonNode, map: MutableMap<OriginalPropertyKey, String?>) {
        map[OriginalPropertyKey(fieldName, MappingType.NATIONAL_LIBRARY)] = bookNode.get(fieldName)?.asText()
    }
}