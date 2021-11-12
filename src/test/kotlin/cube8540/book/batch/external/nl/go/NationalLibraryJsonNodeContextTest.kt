package cube8540.book.batch.external.nl.go

import cube8540.book.batch.book.domain.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NationalLibraryJsonNodeContextTest {

    private val publisherMapper: PublisherRawMapper = mockk(relaxed = true)

    @Test
    fun `extract isbn when isbn is null`() {
        val bookNode = createBookJsonNode(isbn = null)
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractIsbn()
        assertThat(result).isEqualTo(defaultSeriesIsbn)
    }

    @Test
    fun `extract isbn when isbn is empty`() {
        val bookNode = createBookJsonNode(isbn = "")
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractIsbn()
        assertThat(result).isEqualTo(defaultSeriesIsbn)
    }

    @Test
    fun `extract isbn when isbn is not null and not empty`() {
        val bookNode = createBookJsonNode()
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractIsbn()
        assertThat(result).isEqualTo(defaultIsbn)
    }

    @Test
    fun `extract book title when original title has volume`() {
        val volume = 1
        val titleWithVolume = "$defaultTitle $volume"

        val bookNode = createBookJsonNode(title = titleWithVolume, seriesNo = volume)
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractTitle()
        assertThat(result).isEqualTo(titleWithVolume)
    }

    @Test
    fun `extract book title when original title has volume, text`() {
        val volume = 1
        val titleWithVolumeAndText = "$defaultTitle ${volume}권"

        val bookNode = createBookJsonNode(title = titleWithVolumeAndText, seriesNo = volume)
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractTitle()
        assertThat(result).startsWith(defaultTitle).endsWith(volume.toString())
    }

    @Test
    fun `extract book title when original title has volume and json node has not volume`() {
        val titleWithVolume = "$defaultTitle 1"

        val bookNode = createBookJsonNode(title = titleWithVolume, seriesNo = null)
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractTitle()
        assertThat(result).isEqualTo(titleWithVolume)
    }

    @Test
    fun `extract book title when original title has volume, text and json node has not volume`() {
        val volume = 1
        val titleWithVolumeAndText = "$defaultTitle ${volume}권"

        val bookNode = createBookJsonNode(title = titleWithVolumeAndText, seriesNo = null)
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractTitle()
        assertThat(result).startsWith(defaultTitle).endsWith(volume.toString())
    }

    @Test
    fun `extract book title`() {
        val volume = 1

        val bookNode = createBookJsonNode(seriesNo = volume)
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractTitle()
        assertThat(result).startsWith(defaultTitle).endsWith(volume.toString())
    }

    @Test
    fun `extract publisher code`() {
        val bookNode = createBookJsonNode(publisher = "publisherCode")
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        every { publisherMapper.mapping("publisherCode") } returns "extractedPublisherCode"

        val result = context.extractPublisher()
        assertThat(result).isEqualTo("extractedPublisherCode")
    }

    @Test
    fun `extract publish date is null`() {
        val bookNode = createBookJsonNode(realPublishDate = null)
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractPublishDate()
        assertThat(result).isEqualTo(defaultPublishDate)
    }

    @Test
    fun `extract publish date is empty`() {
        val bookNode = createBookJsonNode(realPublishDate = "")
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractPublishDate()
        assertThat(result).isEqualTo(defaultPublishDate)
    }

    @Test
    fun `extract publish date`() {
        val bookNode = createBookJsonNode()
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractPublishDate()
        assertThat(result).isEqualTo(defaultRealPublishDate)
    }

    @Test
    fun `extract series isbn when set isbn is null`() {
        val bookNode = createBookJsonNode(seriesIsbn = null)
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractSeriesIsbn()
        assertThat(result).isNull()
    }

    @Test
    fun `extract series isbn when set isbn is empty`() {
        val bookNode = createBookJsonNode(seriesIsbn = "")
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractSeriesIsbn()
        assertThat(result).isNull()
    }

    @Test
    fun `extract series isbn`() {
        val bookNode = createBookJsonNode()
        val context = NationalLibraryJsonNodeContext(bookNode, publisherMapper)

        val result = context.extractSeriesIsbn()
        assertThat(result).isEqualTo(defaultSeriesIsbn)
    }
}