package cube8540.book.batch.external.nl.go

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.expectedTitle
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.isbn0
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.isbn1
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.publishPreDate
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.publisherCode
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.realPublishDate
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.responsePublishPreDate
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.responsePublisher
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.responseRealPublishDate
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.title
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.titleWithVolume
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.titleWithVolumeAndText
import cube8540.book.batch.external.nl.go.NationalLibraryJsonNodeContextTestEnvironment.volume
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NationalLibraryJsonNodeContextTest {

    private val jsonNode: JsonNode = mockk(relaxed = true)
    private val publisherMapper: PublisherRawMapper = mockk(relaxed = true)

    private val context = NationalLibraryJsonNodeContext(jsonNode, publisherMapper)

    @Test
    fun `resolve isbn when isbn is null`() {
        every { jsonNode.get(NationalLibraryAPIResponseNames.isbn) } returns null
        every { jsonNode.get(NationalLibraryAPIResponseNames.setIsbn) } returns TextNode(isbn0)

        val result = context.resolveIsbn()
        assertThat(result).isEqualTo(isbn0)
    }

    @Test
    fun `resolve isbn when isbn is empty`() {
        every { jsonNode.get(NationalLibraryAPIResponseNames.isbn) } returns TextNode("")
        every { jsonNode.get(NationalLibraryAPIResponseNames.setIsbn) } returns TextNode(isbn0)

        val result = context.resolveIsbn()
        assertThat(result).isEqualTo(isbn0)
    }

    @Test
    fun `resolve isbn when isbn is not null and not empty`() {
        every { jsonNode.get(NationalLibraryAPIResponseNames.isbn) } returns TextNode(isbn0)
        every { jsonNode.get(NationalLibraryAPIResponseNames.setIsbn) } returns TextNode(isbn1)

        val result = context.resolveIsbn()
        assertThat(result).isEqualTo(isbn0)
    }

    @Test
    fun `resolve book title when original title has volume`() {
        every { jsonNode.get(NationalLibraryAPIResponseNames.title) } returns TextNode(titleWithVolume)
        every { jsonNode.get(NationalLibraryAPIResponseNames.seriesNo) } returns IntNode(volume)

        val result = context.resolveTitle()
        assertThat(result).isEqualTo(expectedTitle)
    }

    @Test
    fun `resolve book title when original title has volume, text`() {
        every { jsonNode.get(NationalLibraryAPIResponseNames.title) } returns TextNode(titleWithVolumeAndText)
        every { jsonNode.get(NationalLibraryAPIResponseNames.seriesNo) } returns IntNode(volume)

        val result = context.resolveTitle()
        assertThat(result).isEqualTo(expectedTitle)
    }

    @Test
    fun `resolve book title when original title has volume and json node has not volume`() {
        every { jsonNode.get(NationalLibraryAPIResponseNames.title) } returns TextNode(titleWithVolume)
        every { jsonNode.get(NationalLibraryAPIResponseNames.seriesNo) } returns null

        val result = context.resolveTitle()
        assertThat(result).isEqualTo(expectedTitle)
    }

    @Test
    fun `extract book title when original title has volume, text and json node has not volume`() {
        every { jsonNode.get(NationalLibraryAPIResponseNames.title) } returns TextNode(titleWithVolumeAndText)
        every { jsonNode.get(NationalLibraryAPIResponseNames.seriesNo) } returns null

        val result = context.resolveTitle()
        assertThat(result).isEqualTo(expectedTitle)
    }

    @Test
    fun `extract book title`() {
        every { jsonNode.get(NationalLibraryAPIResponseNames.title) } returns TextNode(title)
        every { jsonNode.get(NationalLibraryAPIResponseNames.seriesNo) } returns IntNode(volume)

        val result = context.resolveTitle()
        assertThat(result).isEqualTo(expectedTitle)
    }

    @Test
    fun `extract publisher code`() {
        every { jsonNode.get(NationalLibraryAPIResponseNames.publisher) } returns TextNode(responsePublisher)
        every { publisherMapper.mapping(responsePublisher) } returns publisherCode

        val result = context.resolvePublisher()
        assertThat(result).isEqualTo(publisherCode)
    }

    @Test
    fun `extract publish date is null`() {
        every { jsonNode.get(NationalLibraryAPIResponseNames.realPublishDate) } returns null
        every { jsonNode.get(NationalLibraryAPIResponseNames.publishPreDate) } returns TextNode(responsePublishPreDate)

        val result = context.resolvePublishDate()
        assertThat(result).isEqualTo(publishPreDate)
    }

    @Test
    fun `extract publish date is empty`() {
        every { jsonNode.get(NationalLibraryAPIResponseNames.realPublishDate) } returns TextNode("")
        every { jsonNode.get(NationalLibraryAPIResponseNames.publishPreDate) } returns TextNode(responsePublishPreDate)

        val result = context.resolvePublishDate()
        assertThat(result).isEqualTo(publishPreDate)
    }

    @Test
    fun `extract publish date`() {
        every { jsonNode.get(NationalLibraryAPIResponseNames.realPublishDate) } returns TextNode(responseRealPublishDate)
        every { jsonNode.get(NationalLibraryAPIResponseNames.publishPreDate) } returns TextNode(responsePublishPreDate)

        val result = context.resolvePublishDate()
        assertThat(result).isEqualTo(realPublishDate)
    }
}