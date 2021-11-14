package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.book.domain.*
import cube8540.book.batch.getQueryParams
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Test

class KyoboBookJsoupDocumentContextTest {

    private val divisionMapper: DivisionRawMapper = mockk(relaxed = true)

    @Test
    fun `extract publish date`() {
        val document = createDocument()
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractPublishDate()
        assertThat(result).isEqualTo(defaultPublishDate)
    }

    @Test
    fun `extract authors`() {
        val documentAuthors = " author 0001 , author 0002 "
        val authors = setOf("author 0001", "author 0002")

        val document = createDocument(authors = documentAuthors)
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractAuthors()
        assertThat(result).isEqualTo(authors)
    }

    @Test
    fun `extract categories`() {
        val documentCategories = "010101"
        val categories = listOf("01", "0101", "010101")

        val mappedCategories = listOf("category01", "category02", "category03")

        val document = createDocument(categoryCode = documentCategories)
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        every { divisionMapper.mapping(categories) } returns mappedCategories

        val result = context.extractDivisions()
        assertThat(result).containsExactlyElementsOf(mappedCategories)
    }

    @Test
    fun `extract series code sBarcode is null`() {
        val document = createDocument(seriesBarcode = null)

        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractSeriesCode()
        assertThat(result).isEqualTo(defaultABarcode)
    }

    @Test
    fun `extract series code sBarcode is empty`() {
        val document = createDocument(seriesBarcode = "")
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractSeriesCode()
        assertThat(result).isEqualTo(defaultABarcode)
    }

    @Test
    fun `extract series code sBarcode is not null and is not empty`() {
        val document = createDocument()
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractSeriesCode()
        assertThat(result).isEqualTo(defaultSeriesCode)
    }

    @Test
    fun `extract series code aBarcode is empty`() {
        val document = createDocument(seriesBarcode = null, aBarcode = "")
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractSeriesCode()
        assertThat(result).isNull()
    }

    @Test
    fun `extract thumbnail`() {
        val document = createDocument()
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractThumbnail()
        assertThat(result)
            .isEqualTo(Thumbnail(largeThumbnail = defaultLargeThumbnail, mediumThumbnail = defaultMediumThumbnail))
    }

    @Test
    fun `extract document with br tag`() {
        val descriptionText = "description0000000"
        val document = createDocument(description = "$descriptionText<br/>")
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractDescription()
        assertThat(result).isEqualTo("$descriptionText \\n")
    }

    @Test
    fun `extract document with p tag`() {
        val descriptionText = "description0000000"
        val document = createDocument(description = "<p>$descriptionText</p>")
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractDescription()
        assertThat(result).isEqualTo("\\n\\n$descriptionText")
    }

    @Test
    fun `extract document with new line`() {
        val descriptionText = "description0000000\\n"
        val document = createDocument(description = descriptionText)
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractDescription()
        assertThat(result).isEqualTo(descriptionText)
    }

    @Test
    fun `extract document is empty`() {
        val document = createDocument(description = null)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractDescription()
        assertThat(result).isNull()
    }

    @Test
    fun `extract index br tag with sapce`() {
        val index = "index0001<br >index0002<br >index0003<br >index0004<br >"
        val document = createDocument(indexHtml = index)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractIndex()
        assertThat(result).containsExactly("index0001", "index0002", "index0003", "index0004")
    }

    @Test
    fun `extract index br tag with out space`() {
        val index = "index0001<br>index0002<br>index0003<br>index0004<br>"
        val document = createDocument(indexHtml = index)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractIndex()
        assertThat(result).containsExactly("index0001", "index0002", "index0003", "index0004")
    }

    @Test
    fun `extract index br tag with slash`() {
        val index = "index0001<br/>index0002<br/>index0003<br/>index0004<br/>"
        val document = createDocument(indexHtml = index)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractIndex()
        assertThat(result).containsExactly("index0001", "index0002", "index0003", "index0004")
    }

    @Test
    fun `extract index title with escape characters`() {
        val index = "\t\r\nindex0001<br/>\n\t\rindex0002<br/>\t\r\nindex0003<br/>\t\n\rindex0004<br/>"
        val document = createDocument(indexHtml = index)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractIndex()
        assertThat(result).containsExactly("index0001", "index0002", "index0003", "index0004")
    }

    @Test
    fun `extract index title is empty`() {
        val index = "\t\r\nindex0001<br/><br/>\t\r\nindex0003<br/><br/>"
        val document = createDocument(indexHtml = index)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractIndex()
        assertThat(result).containsExactly("index0001", "index0003")
    }

    @Test
    fun `extract index title is multiple space`() {
        val index = "\t\r\n  index 0001<br/>\n\t\rindex 0002<br/>\t\r\n    index 0003<br/>\t\n\rindex 0004<br/>"
        val document = createDocument(indexHtml = index)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractIndex()
        assertThat(result).containsExactly("index 0001", "index 0002", "index 0003", "index 0004")
    }

    @Test
    fun `extract index is empty`() {
        val document = createDocument(indexHtml = null)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractIndex()
        assertThat(result).isNull()
    }

    @Test
    fun `extract external link`() {
        val document = createDocument(isbn = defaultIsbn, originalPrice = defaultOriginalPrice, salePrice = defaultSalePrice)
            .outputSettings(Document.OutputSettings().prettyPrint(false))
        val context = KyoboBookJsoupDocumentContext(document, divisionMapper)

        val result = context.extractExternalLink()
        assertThat(result.keys).containsExactly(MappingType.KYOBO)
        assertThat(result[MappingType.KYOBO]!!.productDetailPage.host)
            .isEqualTo(KyoboBookRequestNames.kyoboHost)
        assertThat(result[MappingType.KYOBO]!!.productDetailPage.path)
            .isEqualTo(KyoboBookRequestNames.kyoboBookDetailsPath)
        assertThat(result[MappingType.KYOBO]!!.productDetailPage.getQueryParams())
            .containsExactly(entry(KyoboBookRequestNames.isbn, listOf(defaultIsbn)))
        assertThat(result[MappingType.KYOBO]!!.originalPrice).isEqualTo(defaultOriginalPrice)
        assertThat(result[MappingType.KYOBO]!!.salePrice).isEqualTo(defaultSalePrice)
    }
}