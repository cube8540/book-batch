package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.book.domain.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
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
        assertThat(result).containsAll(authors)
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
        assertThat(result).containsAll(mappedCategories)
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
}