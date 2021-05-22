package cube8540.book.batch.external.nl.go

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import cube8540.book.batch.domain.BookDetailsContext
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.exception.ErrorCodeExternalExceptionCreator

class NationalLibraryAPIDeserializer(private val publisherRawMapper: PublisherRawMapper): StdDeserializer<BookAPIResponse>(BookAPIResponse::class.java) {

    internal var exceptionCreator: ErrorCodeExternalExceptionCreator = NationalLibraryAPIErrorCodeExceptionCreator()

    override fun deserialize(p0: JsonParser, p1: DeserializationContext?): BookAPIResponse {
        val responseNode = p0.codec.readTree<JsonNode>(p0)

        // 국립중앙도서관 API는 요청에 실패하여도 응답값을 200으로 내려주기 때문에 응답값을 체크하여 에러 처리를 해야 한다.
        if (responseNode.get(NationalLibraryAPIResponseNames.result)?.asText() == "ERROR") {
            throw exceptionCreator.create(
                responseNode.get(NationalLibraryAPIResponseNames.errorCode).asText(),
                responseNode.get(NationalLibraryAPIResponseNames.errorMessage).asText()
            )
        }

        val page = responseNode.get(NationalLibraryAPIResponseNames.pageNo).asText()
        val totalCount = responseNode.get(NationalLibraryAPIResponseNames.totalCount).asText()

        val bookDetails: List<BookDetailsContext> = when (val documents = responseNode.get(NationalLibraryAPIResponseNames.documents)) {
            null -> emptyList()
            else -> (documents as ArrayNode).map { NationalLibraryJsonNodeContext(it, publisherRawMapper) }
        }

        return BookAPIResponse(totalCount.toLong(), page.toLong(), bookDetails)
    }
}