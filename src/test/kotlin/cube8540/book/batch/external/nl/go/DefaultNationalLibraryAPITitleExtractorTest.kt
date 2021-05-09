package cube8540.book.batch.external.nl.go

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultNationalLibraryAPITitleExtractorTest {
    private val title = "권 1 권 권 권권권"
    private val titleWithVolume1 = "$title 1"
    private val titleWithVolume2 = "$title 1권"
    private val volume = 1
    private val expectedTitle = "$title $volume"

    private val extractor = DefaultNationalLibraryAPITitleExtractor()

    @Test
    fun `extract book title`() {
        val jsonNode: JsonNode = mockk(relaxed = true)

        every { jsonNode.get(NationalLibraryAPIResponseNames.title) } returns TextNode(title)
        every { jsonNode.get(NationalLibraryAPIResponseNames.seriesNo) } returns IntNode(volume)

        val result = extractor.extract(jsonNode)
        assertThat(result).isEqualTo(expectedTitle)
    }

    @Test
    fun `extract book title when original title has volume`() {
        val jsonNode: JsonNode = mockk(relaxed = true)

        every { jsonNode.get(NationalLibraryAPIResponseNames.title) } returns TextNode(titleWithVolume1)
        every { jsonNode.get(NationalLibraryAPIResponseNames.seriesNo) } returns IntNode(volume)

        val result = extractor.extract(jsonNode)
        assertThat(result).isEqualTo(expectedTitle)
    }

    @Test
    fun `extract book title when original title has volume, text`() {
        val jsonNode: JsonNode = mockk(relaxed = true)

        every { jsonNode.get(NationalLibraryAPIResponseNames.title) } returns TextNode(titleWithVolume2)
        every { jsonNode.get(NationalLibraryAPIResponseNames.seriesNo) } returns IntNode(volume)

        val result = extractor.extract(jsonNode)
        assertThat(result).isEqualTo(expectedTitle)
    }

    @Test
    fun `extract book title when original title has volume and json node has not volume`() {
        val jsonNode: JsonNode = mockk(relaxed = true)

        every { jsonNode.get(NationalLibraryAPIResponseNames.title) } returns TextNode(titleWithVolume1)
        every { jsonNode.get(NationalLibraryAPIResponseNames.seriesNo) } returns null

        val result = extractor.extract(jsonNode)
        assertThat(result).isEqualTo(expectedTitle)
    }

    @Test
    fun `extract book title when original title has volume, text and json node has not volume`() {
        val jsonNode: JsonNode = mockk(relaxed = true)

        every { jsonNode.get(NationalLibraryAPIResponseNames.title) } returns TextNode(titleWithVolume2)
        every { jsonNode.get(NationalLibraryAPIResponseNames.seriesNo) } returns null

        val result = extractor.extract(jsonNode)
        assertThat(result).isEqualTo(expectedTitle)
    }
}