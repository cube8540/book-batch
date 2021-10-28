package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.book.domain.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Test

class KyoboBookJsoupDocumentContextTest {

    private val divisionMapper: DivisionRawMapper = mockk(relaxed = true)

    @Test
    fun `resolve authors`() {
        val documentAuthors = " author 0001 , author 0002 "
        val authors = setOf("author 0001", "author 0002")

        val document = createDocument(authors = documentAuthors)
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveAuthors()
        assertThat(result).isEqualTo(authors)
    }

    @Test
    fun `resolve categories`() {
        val documentCategories = "010101"
        val categories = listOf("01", "0101", "010101")

        val mappedCategories = listOf("category01", "category02", "category03")

        val document = createDocument(categoryCode = documentCategories)
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        every { divisionMapper.mapping(categories) } returns mappedCategories

        val result = context.resolveDivisions()
        assertThat(result).containsExactlyElementsOf(mappedCategories)
    }

    @Test
    fun `resolve series code sBarcode is null`() {
        val document = createDocument(seriesBarcode = null)

        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveSeriesCode()
        assertThat(result).isEqualTo(defaultABarcode)
    }

    @Test
    fun `resolve series code sBarcode is empty`() {
        val document = createDocument(seriesBarcode = "")
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveSeriesCode()
        assertThat(result).isEqualTo(defaultABarcode)
    }

    @Test
    fun `resolve series code sBarcode is not null and is not empty`() {
        val document = createDocument()
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveSeriesCode()
        assertThat(result).isEqualTo(defaultSeriesCode)
    }

    @Test
    fun `resolve series code aBarcode is empty`() {
        val document = createDocument(seriesBarcode = null, aBarcode = "")
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveSeriesCode()
        assertThat(result).isNull()
    }

    @Test
    fun `resolve thumbnail`() {
        val document = createDocument()
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveThumbnail()
        assertThat(result)
            .isEqualTo(Thumbnail(largeThumbnail = defaultLargeThumbnail, mediumThumbnail = defaultMediumThumbnail))
    }

    @Test
    fun `resolve document with br tag`() {
        val descriptionText = "description0000000"
        val document = createDocument(description = "$descriptionText<br/>")
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveDescription()
        assertThat(result).isEqualTo("$descriptionText \\n")
    }

    @Test
    fun `resolve document with p tag`() {
        val descriptionText = "description0000000"
        val document = createDocument(description = "<p>$descriptionText</p>")
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveDescription()
        assertThat(result).isEqualTo("\\n\\n$descriptionText")
    }

    @Test
    fun `resolve document with new line`() {
        val descriptionText = "description0000000\\n"
        val document = createDocument(description = descriptionText)
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveDescription()
        assertThat(result).isEqualTo(descriptionText)
    }

    @Test
    fun `resolve document is empty`() {
        val document = createDocument(description = null)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveDescription()
        assertThat(result).isNull()
    }

    @Test
    fun `resolve index br tag with sapce`() {
        val index = "index0001<br >index0002<br >index0003<br >index0004<br >"
        val document = createDocument(indexHtml = index)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveIndex()
        assertThat(result).containsExactly("index0001", "index0002", "index0003", "index0004")
    }

    @Test
    fun `resolve index br tag with out space`() {
        val index = "index0001<br>index0002<br>index0003<br>index0004<br>"
        val document = createDocument(indexHtml = index)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveIndex()
        assertThat(result).containsExactly("index0001", "index0002", "index0003", "index0004")
    }

    @Test
    fun `resolve index br tag with slash`() {
        val index = "index0001<br/>index0002<br/>index0003<br/>index0004<br/>"
        val document = createDocument(indexHtml = index)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveIndex()
        assertThat(result).containsExactly("index0001", "index0002", "index0003", "index0004")
    }

    @Test
    fun `resolve index title with escape characters`() {
        val index = "\t\r\nindex0001<br/>\n\t\rindex0002<br/>\t\r\nindex0003<br/>\t\n\rindex0004<br/>"
        val document = createDocument(indexHtml = index)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveIndex()
        assertThat(result).containsExactly("index0001", "index0002", "index0003", "index0004")
    }

    @Test
    fun `resolve index title is empty`() {
        val index = "\t\r\nindex0001<br/><br/>\t\r\nindex0003<br/><br/>"
        val document = createDocument(indexHtml = index)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveIndex()
        assertThat(result).containsExactly("index0001", "index0003")
    }

    @Test
    fun `resolve index title is multiple space`() {
        val index = "\t\r\n  index 0001<br/>\n\t\rindex 0002<br/>\t\r\n    index 0003<br/>\t\n\rindex 0004<br/>"
        val document = createDocument(indexHtml = index)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveIndex()
        assertThat(result).containsExactly("index 0001", "index 0002", "index 0003", "index 0004")
    }

    @Test
    fun `resolve index is empty`() {
        val document = createDocument(indexHtml = null)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.resolveIndex()
        assertThat(result).isNull()
    }
}