package cube8540.book.batch.external.nl.go

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.TextNode
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.OriginalPropertyKey
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.additionalCode0
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.author
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.isbn0
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.isbn1
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.isbn2
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.pageNumber
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.publishPreDate
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.publisherCode
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.realPublishDate
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.responsePublishPreDate
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.responsePublisher
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.responseRealPublishDate
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.seriesNo
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.setAdditionalCode
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.setExpression
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.setIsbn0
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.subject
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.title0
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.title1
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.title2
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.totalCount
import cube8540.book.batch.external.nl.go.NationalLibraryAPIDeserializerTestEnvironment.updateDate
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NationalLibraryAPIDeserializerTest {

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    private val deserializer = NationalLibraryAPIDeserializer(publisherRawMapper)

    @Test
    fun `response deserialization when book is empty`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)

        every { responseNode.get(NationalLibraryAPIResponseNames.pageNo) } returns TextNode(pageNumber)
        every { responseNode.get(NationalLibraryAPIResponseNames.totalCount) } returns TextNode(totalCount)
        every { responseNode.get(NationalLibraryAPIResponseNames.documents) } returns null

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.page).isEqualTo(pageNumber.toLong())
        assertThat(response.totalCount).isEqualTo(totalCount.toLong())
        assertThat(response.books).isEqualTo(emptyList<BookDetails>())
    }

    @Test
    fun `response book real publish date is null`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)
        val documentsNode = ArrayNode(mockk(relaxed = true))
        val bookNode: JsonNode = mockk(relaxed = true)

        every { bookNode.get(NationalLibraryAPIResponseNames.isbn) } returns TextNode(isbn0)
        every { bookNode.get(NationalLibraryAPIResponseNames.realPublishDate) } returns null
        every { bookNode.get(NationalLibraryAPIResponseNames.publishPreDate) } returns TextNode(responsePublishPreDate)
        documentsNode.add(bookNode)

        every { responseNode.get(NationalLibraryAPIResponseNames.pageNo) } returns TextNode(pageNumber)
        every { responseNode.get(NationalLibraryAPIResponseNames.totalCount) } returns TextNode(totalCount)
        every { responseNode.get(NationalLibraryAPIResponseNames.documents) } returns documentsNode

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        val deserializedBook = response.books[0]
        assertThat(deserializedBook.publishDate).isEqualTo(publishPreDate)
    }

    @Test
    fun `response book real publish date is empty`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)
        val documentsNode = ArrayNode(mockk(relaxed = true))
        val bookNode: JsonNode = mockk(relaxed = true)

        every { bookNode.get(NationalLibraryAPIResponseNames.isbn) } returns TextNode(isbn0)
        every { bookNode.get(NationalLibraryAPIResponseNames.realPublishDate) } returns TextNode("")
        every { bookNode.get(NationalLibraryAPIResponseNames.publishPreDate) } returns TextNode(responsePublishPreDate)
        documentsNode.add(bookNode)

        every { responseNode.get(NationalLibraryAPIResponseNames.pageNo) } returns TextNode(pageNumber)
        every { responseNode.get(NationalLibraryAPIResponseNames.totalCount) } returns TextNode(totalCount)
        every { responseNode.get(NationalLibraryAPIResponseNames.documents) } returns documentsNode

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        val deserializedBook = response.books[0]
        assertThat(deserializedBook.publishDate).isEqualTo(publishPreDate)
    }

    @Test
    fun `response book real publish date is not null`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)
        val documentsNode = ArrayNode(mockk(relaxed = true))
        val bookNode: JsonNode = mockk(relaxed = true)

        every { bookNode.get(NationalLibraryAPIResponseNames.isbn) } returns TextNode(isbn0)
        every { bookNode.get(NationalLibraryAPIResponseNames.realPublishDate) } returns TextNode(responseRealPublishDate)
        every { bookNode.get(NationalLibraryAPIResponseNames.publishPreDate) } returns TextNode(responsePublishPreDate)
        documentsNode.add(bookNode)

        every { responseNode.get(NationalLibraryAPIResponseNames.pageNo) } returns TextNode(pageNumber)
        every { responseNode.get(NationalLibraryAPIResponseNames.totalCount) } returns TextNode(totalCount)
        every { responseNode.get(NationalLibraryAPIResponseNames.documents) } returns documentsNode

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        val deserializedBook = response.books[0]
        assertThat(deserializedBook.publishDate).isEqualTo(realPublishDate)
    }

    @Test
    fun `response deserialization`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)
        val documentsNode = ArrayNode(mockk(relaxed = true))
        val bookNode0: JsonNode = mockk(relaxed = true)
        val bookNode1: JsonNode = mockk(relaxed = true)
        val bookNode2: JsonNode = mockk(relaxed = true)

        every { bookNode0.get(NationalLibraryAPIResponseNames.isbn) } returns TextNode(isbn0)
        every { bookNode0.get(NationalLibraryAPIResponseNames.title) } returns TextNode(title0)
        every { bookNode0.get(NationalLibraryAPIResponseNames.publisher) } returns TextNode(responsePublisher)
        every { bookNode0.get(NationalLibraryAPIResponseNames.realPublishDate) } returns TextNode(responseRealPublishDate)
        every { bookNode0.get(NationalLibraryAPIResponseNames.publishPreDate) } returns TextNode(responsePublishPreDate)
        documentsNode.add(bookNode0)

        every { bookNode1.get(NationalLibraryAPIResponseNames.isbn) } returns TextNode(isbn1)
        every { bookNode1.get(NationalLibraryAPIResponseNames.title) } returns TextNode(title1)
        every { bookNode1.get(NationalLibraryAPIResponseNames.publisher) } returns TextNode(responsePublisher)
        every { bookNode1.get(NationalLibraryAPIResponseNames.realPublishDate) } returns TextNode(responseRealPublishDate)
        every { bookNode1.get(NationalLibraryAPIResponseNames.publishPreDate) } returns TextNode(responsePublishPreDate)
        documentsNode.add(bookNode1)

        every { bookNode2.get(NationalLibraryAPIResponseNames.isbn) } returns TextNode(isbn2)
        every { bookNode2.get(NationalLibraryAPIResponseNames.title) } returns TextNode(title2)
        every { bookNode2.get(NationalLibraryAPIResponseNames.publisher) } returns TextNode(responsePublisher)
        every { bookNode2.get(NationalLibraryAPIResponseNames.realPublishDate) } returns TextNode(responseRealPublishDate)
        every { bookNode2.get(NationalLibraryAPIResponseNames.publishPreDate) } returns TextNode(responsePublishPreDate)
        documentsNode.add(bookNode2)

        every { responseNode.get(NationalLibraryAPIResponseNames.pageNo) } returns TextNode(pageNumber)
        every { responseNode.get(NationalLibraryAPIResponseNames.totalCount) } returns TextNode(totalCount)
        every { responseNode.get(NationalLibraryAPIResponseNames.documents) } returns documentsNode

        every { publisherRawMapper.mapping(responsePublisher) } returns publisherCode

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.page).isEqualTo(pageNumber.toLong())
        assertThat(response.totalCount).isEqualTo(totalCount.toLong())

        assertThat(response.books[0].isbn).isEqualTo(isbn0)
        assertThat(response.books[0].title).isEqualTo(title0)
        assertThat(response.books[0].publisher).isEqualTo(publisherCode)
        assertThat(response.books[0].publishDate).isEqualTo(realPublishDate)

        assertThat(response.books[1].isbn).isEqualTo(isbn1)
        assertThat(response.books[1].title).isEqualTo(title1)
        assertThat(response.books[1].publisher).isEqualTo(publisherCode)
        assertThat(response.books[1].publishDate).isEqualTo(realPublishDate)

        assertThat(response.books[2].isbn).isEqualTo(isbn2)
        assertThat(response.books[2].title).isEqualTo(title2)
        assertThat(response.books[2].publisher).isEqualTo(publisherCode)
        assertThat(response.books[2].publishDate).isEqualTo(realPublishDate)
    }

    @Test
    fun `save original data`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: JsonMapper = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)
        val documentsNode = ArrayNode(mockk(relaxed = true))
        val bookNode: JsonNode = mockk(relaxed = true)

        every { bookNode.get(NationalLibraryAPIResponseNames.isbn) } returns TextNode(isbn0)
        every { bookNode.get(NationalLibraryAPIResponseNames.title) } returns TextNode(title0)
        every { bookNode.get(NationalLibraryAPIResponseNames.publisher) } returns TextNode(responsePublisher)
        every { bookNode.get(NationalLibraryAPIResponseNames.realPublishDate) } returns TextNode(responseRealPublishDate)
        every { bookNode.get(NationalLibraryAPIResponseNames.publishPreDate) } returns TextNode(responsePublishPreDate)
        every { bookNode.get(NationalLibraryAPIResponseNames.setIsbn) } returns TextNode(setIsbn0)
        every { bookNode.get(NationalLibraryAPIResponseNames.additionalCode) } returns TextNode(additionalCode0)
        every { bookNode.get(NationalLibraryAPIResponseNames.setAdditionalCode) } returns TextNode(setAdditionalCode)
        every { bookNode.get(NationalLibraryAPIResponseNames.seriesNo) } returns TextNode(seriesNo)
        every { bookNode.get(NationalLibraryAPIResponseNames.setExpression) } returns TextNode(setExpression)
        every { bookNode.get(NationalLibraryAPIResponseNames.subject) } returns TextNode(subject)
        every { bookNode.get(NationalLibraryAPIResponseNames.author) } returns TextNode(author)
        every { bookNode.get(NationalLibraryAPIResponseNames.updateDate) } returns TextNode(updateDate)
        documentsNode.add(bookNode)

        every { responseNode.get(NationalLibraryAPIResponseNames.pageNo) } returns TextNode(pageNumber)
        every { responseNode.get(NationalLibraryAPIResponseNames.totalCount) } returns TextNode(totalCount)
        every { responseNode.get(NationalLibraryAPIResponseNames.documents) } returns documentsNode

        every { publisherRawMapper.mapping(responsePublisher) } returns publisherCode

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns responseNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        val original = response.books[0].original!!
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.isbn, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(isbn0)
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.title, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(title0)
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.publisher, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(responsePublisher)
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.realPublishDate, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(responseRealPublishDate)
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.publishPreDate, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(responsePublishPreDate)
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.setIsbn, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(setIsbn0)
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.additionalCode, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(additionalCode0)
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.setAdditionalCode, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(setAdditionalCode)
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.seriesNo, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(seriesNo)
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.setExpression, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(setExpression)
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.subject, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(subject)
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.author, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(author)
        assertThat(original[OriginalPropertyKey(NationalLibraryAPIResponseNames.updateDate, MappingType.NATIONAL_LIBRARY)])
            .isEqualTo(updateDate)
    }
}