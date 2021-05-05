package cube8540.book.batch.external.naver.com

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.OriginalPropertyKey
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.author
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.description
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.discount
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.display
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.image
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.isbn0
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.isbn1
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.isbn2
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.link
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.price
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.publishDate
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.publisher
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.publisherCode
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.responseIsbn0
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.responseIsbn1
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.responseIsbn2
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.responsePublishDate
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.start
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.title0
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.title1
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.title2
import cube8540.book.batch.external.naver.com.NaverBookAPIDeserializerTestEnvironment.totalCount
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

class NaverBookAPIDeserializerTest {

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    private val deserializer = NaverBookAPIDeserializer(publisherRawMapper)

    @Test
    fun `response deserialization when book is empty`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: XmlMapper = mockk(relaxed = true)
        val channelNode: JsonNode = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)

        every { responseNode.get(NaverBookAPIResponseNames.totalCount) } returns IntNode(totalCount)
        every { responseNode.get(NaverBookAPIResponseNames.start) } returns IntNode(start)
        every { responseNode.get(NaverBookAPIResponseNames.display) } returns IntNode(display)
        every { responseNode.get(NaverBookAPIResponseNames.item) } returns null
        every { channelNode.get(NaverBookAPIResponseNames.channel) } returns responseNode

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns channelNode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.page).isEqualTo(start.toLong())
        assertThat(response.totalCount).isEqualTo(totalCount.toLong())
        assertThat(response.books).isEqualTo(emptyList<BookDetails>())
    }

    @Test
    fun `response deserialization when book node is not array node`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: XmlMapper = mockk(relaxed = true)
        val channelNode: JsonNode = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)
        val bookNode: JsonNode = mockk(relaxed = true)

        every { bookNode.get(NaverBookAPIResponseNames.isbn) } returns TextNode(responseIsbn0)
        every { bookNode.get(NaverBookAPIResponseNames.title) } returns TextNode(title0)
        every { bookNode.get(NaverBookAPIResponseNames.price) } returns IntNode(price)
        every { bookNode.get(NaverBookAPIResponseNames.publisher) } returns TextNode(publisher)
        every { bookNode.get(NaverBookAPIResponseNames.publishDate) } returns TextNode(responsePublishDate)
        every { bookNode.get(NaverBookAPIResponseNames.image) } returns TextNode(image)

        every { responseNode.get(NaverBookAPIResponseNames.start) } returns IntNode(start)
        every { responseNode.get(NaverBookAPIResponseNames.totalCount) } returns IntNode(totalCount)
        every { responseNode.get(NaverBookAPIResponseNames.display) } returns IntNode(display)
        every { responseNode.get(NaverBookAPIResponseNames.item) } returns bookNode

        every { channelNode.get(NaverBookAPIResponseNames.channel) } returns responseNode

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns channelNode

        every { publisherRawMapper.mapping(publisher) } returns publisherCode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.page).isEqualTo(start.toLong())
        assertThat(response.totalCount).isEqualTo(totalCount.toLong())
        assertThat(response.books.size).isEqualTo(1)
        assertThat(response.books.first().isbn).isEqualTo(isbn0)
        assertThat(response.books.first().title).isEqualTo(title0)
        assertThat(response.books.first().publisher).isEqualTo(publisherCode)
        assertThat(response.books.first().publishDate).isEqualTo(publishDate)
        assertThat(response.books.first().smallThumbnail).isEqualTo(URI.create(image))
    }

    @Test
    fun `response deserialization when book node is array node`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: XmlMapper = mockk(relaxed = true)
        val channelNode: JsonNode = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)
        val bookNode0: JsonNode = mockk(relaxed = true)
        val bookNode1: JsonNode = mockk(relaxed = true)
        val bookNode2: JsonNode = mockk(relaxed = true)
        val bookArrayNode = ArrayNode(mockk(relaxed = true))

        every { bookNode0.get(NaverBookAPIResponseNames.isbn) } returns TextNode(responseIsbn0)
        every { bookNode0.get(NaverBookAPIResponseNames.title) } returns TextNode(title0)
        every { bookNode0.get(NaverBookAPIResponseNames.price) } returns IntNode(price)
        every { bookNode0.get(NaverBookAPIResponseNames.publisher) } returns TextNode(publisher)
        every { bookNode0.get(NaverBookAPIResponseNames.publishDate) } returns TextNode(responsePublishDate)
        every { bookNode0.get(NaverBookAPIResponseNames.image) } returns TextNode(image)
        bookArrayNode.add(bookNode0)

        every { bookNode1.get(NaverBookAPIResponseNames.isbn) } returns TextNode(responseIsbn1)
        every { bookNode1.get(NaverBookAPIResponseNames.title) } returns TextNode(title1)
        every { bookNode1.get(NaverBookAPIResponseNames.price) } returns IntNode(price)
        every { bookNode1.get(NaverBookAPIResponseNames.publisher) } returns TextNode(publisher)
        every { bookNode1.get(NaverBookAPIResponseNames.publishDate) } returns TextNode(responsePublishDate)
        every { bookNode1.get(NaverBookAPIResponseNames.image) } returns TextNode(image)
        bookArrayNode.add(bookNode1)

        every { bookNode2.get(NaverBookAPIResponseNames.isbn) } returns TextNode(responseIsbn2)
        every { bookNode2.get(NaverBookAPIResponseNames.title) } returns TextNode(title2)
        every { bookNode2.get(NaverBookAPIResponseNames.price) } returns IntNode(price)
        every { bookNode2.get(NaverBookAPIResponseNames.publisher) } returns TextNode(publisher)
        every { bookNode2.get(NaverBookAPIResponseNames.publishDate) } returns TextNode(responsePublishDate)
        every { bookNode2.get(NaverBookAPIResponseNames.image) } returns TextNode(image)
        bookArrayNode.add(bookNode2)

        every { responseNode.get(NaverBookAPIResponseNames.start) } returns IntNode(start)
        every { responseNode.get(NaverBookAPIResponseNames.totalCount) } returns IntNode(totalCount)
        every { responseNode.get(NaverBookAPIResponseNames.display) } returns IntNode(display)
        every { responseNode.get(NaverBookAPIResponseNames.item) } returns bookArrayNode

        every { channelNode.get(NaverBookAPIResponseNames.channel) } returns responseNode

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns channelNode

        every { publisherRawMapper.mapping(publisher) } returns publisherCode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        assertThat(response.page).isEqualTo(start.toLong())
        assertThat(response.totalCount).isEqualTo(totalCount.toLong())
        assertThat(response.books.size).isEqualTo(3)

        assertThat(response.books[0].isbn).isEqualTo(isbn0)
        assertThat(response.books[0].title).isEqualTo(title0)
        assertThat(response.books[0].publisher).isEqualTo(publisherCode)
        assertThat(response.books[0].publishDate).isEqualTo(publishDate)
        assertThat(response.books[0].smallThumbnail).isEqualTo(URI.create(image))

        assertThat(response.books[1].isbn).isEqualTo(isbn1)
        assertThat(response.books[1].title).isEqualTo(title1)
        assertThat(response.books[1].publisher).isEqualTo(publisherCode)
        assertThat(response.books[1].publishDate).isEqualTo(publishDate)
        assertThat(response.books[1].smallThumbnail).isEqualTo(URI.create(image))

        assertThat(response.books[2].isbn).isEqualTo(isbn2)
        assertThat(response.books[2].title).isEqualTo(title2)
        assertThat(response.books[2].publisher).isEqualTo(publisherCode)
        assertThat(response.books[2].publishDate).isEqualTo(publishDate)
        assertThat(response.books[2].smallThumbnail).isEqualTo(URI.create(image))
    }

    @Test
    fun `save original data`() {
        val jsonParser: JsonParser = mockk(relaxed = true)
        val codec: XmlMapper = mockk(relaxed = true)
        val channelNode: JsonNode = mockk(relaxed = true)
        val responseNode: JsonNode = mockk(relaxed = true)
        val bookNode: JsonNode = mockk(relaxed = true)

        every { bookNode.get(NaverBookAPIResponseNames.isbn) } returns TextNode(responseIsbn0)
        every { bookNode.get(NaverBookAPIResponseNames.title) } returns TextNode(title0)
        every { bookNode.get(NaverBookAPIResponseNames.link) } returns TextNode(link)
        every { bookNode.get(NaverBookAPIResponseNames.image) } returns TextNode(image)
        every { bookNode.get(NaverBookAPIResponseNames.author) } returns TextNode(author)
        every { bookNode.get(NaverBookAPIResponseNames.price) } returns IntNode(price)
        every { bookNode.get(NaverBookAPIResponseNames.discount) } returns IntNode(discount)
        every { bookNode.get(NaverBookAPIResponseNames.publisher) } returns TextNode(publisher)
        every { bookNode.get(NaverBookAPIResponseNames.publishDate) } returns TextNode(responsePublishDate)
        every { bookNode.get(NaverBookAPIResponseNames.description) } returns TextNode(description)

        every { responseNode.get(NaverBookAPIResponseNames.start) } returns IntNode(start)
        every { responseNode.get(NaverBookAPIResponseNames.totalCount) } returns IntNode(totalCount)
        every { responseNode.get(NaverBookAPIResponseNames.display) } returns IntNode(display)
        every { responseNode.get(NaverBookAPIResponseNames.item) } returns bookNode

        every { channelNode.get(NaverBookAPIResponseNames.channel) } returns responseNode

        every { jsonParser.codec } returns codec
        every { codec.readTree<JsonNode>(jsonParser) } returns channelNode

        every { publisherRawMapper.mapping(publisher) } returns publisherCode

        val response: BookAPIResponse = deserializer.deserialize(jsonParser, mockk(relaxed = true))
        val original = response.books[0].original!!

        assertThat(original[OriginalPropertyKey(NaverBookAPIResponseNames.isbn, MappingType.NAVER_BOOK)]).isEqualTo(responseIsbn0)
        assertThat(original[OriginalPropertyKey(NaverBookAPIResponseNames.title, MappingType.NAVER_BOOK)]).isEqualTo(title0)
        assertThat(original[OriginalPropertyKey(NaverBookAPIResponseNames.link, MappingType.NAVER_BOOK)]).isEqualTo(link)
        assertThat(original[OriginalPropertyKey(NaverBookAPIResponseNames.image, MappingType.NAVER_BOOK)]).isEqualTo(image)
        assertThat(original[OriginalPropertyKey(NaverBookAPIResponseNames.author, MappingType.NAVER_BOOK)]).isEqualTo(author)
        assertThat(original[OriginalPropertyKey(NaverBookAPIResponseNames.price, MappingType.NAVER_BOOK)]).isEqualTo(price.toString())
        assertThat(original[OriginalPropertyKey(NaverBookAPIResponseNames.discount, MappingType.NAVER_BOOK)]).isEqualTo(discount.toString())
        assertThat(original[OriginalPropertyKey(NaverBookAPIResponseNames.publisher, MappingType.NAVER_BOOK)]).isEqualTo(publisher)
        assertThat(original[OriginalPropertyKey(NaverBookAPIResponseNames.publishDate, MappingType.NAVER_BOOK)]).isEqualTo(responsePublishDate)
        assertThat(original[OriginalPropertyKey(NaverBookAPIResponseNames.description, MappingType.NAVER_BOOK)]).isEqualTo(description)
    }
}