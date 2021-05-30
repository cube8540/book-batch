package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.book.domain.DivisionRawMapper
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.author
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.categoryDepth0
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.categoryDepth1
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.categoryDepth2
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.categoryDepthCode0
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.categoryDepthCode1
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.categoryDepthCode2
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.description
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.isbn
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.largeThumbnail
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.mediumThumbnail
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.originalBarcode
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.originalPrice
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.responseAuthor
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.responseCategoryCode
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.secondSeriesBarcode
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.seriesBarcode
import cube8540.book.batch.external.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.title
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Test

class KyoboBookJsoupDocumentContextTest {

    private val divisionMapper: DivisionRawMapper = mockk(relaxed = true)

    @Test
    fun `resolve authors`() {
        val context = KyoboBookJsoupDocumentContext(getDocument(isbn, originalBarcode, seriesBarcode, secondSeriesBarcode), divisionMapper)

        val result = context.resolveAuthors()
        assertThat(result).isEqualTo(author)
    }

    @Test
    fun `resolve categories`() {
        val context = KyoboBookJsoupDocumentContext(getDocument(isbn, originalBarcode, seriesBarcode, secondSeriesBarcode), divisionMapper)

        every { divisionMapper.mapping(listOf(categoryDepth0, categoryDepth1, categoryDepth2)) } returns
                listOf(categoryDepthCode0, categoryDepthCode1, categoryDepthCode2)

        val result = context.resolveDivisions()
        assertThat(result).isEqualTo(setOf(categoryDepthCode0, categoryDepthCode1, categoryDepthCode2))
    }

    @Test
    fun `resolve series code sBarcode is null`() {
        val context = KyoboBookJsoupDocumentContext(getDocument(isbn, originalBarcode, null, secondSeriesBarcode), divisionMapper)

        every { divisionMapper.mapping(listOf(categoryDepth0, categoryDepth1, categoryDepth2)) } returns
                listOf(categoryDepthCode0, categoryDepthCode1, categoryDepthCode2)

        val result = context.resolveSeriesCode()
        assertThat(result).isEqualTo(secondSeriesBarcode)
    }

    @Test
    fun `resolve series code sBarcode is empty`() {
        val context = KyoboBookJsoupDocumentContext(getDocument(isbn, originalBarcode, "", secondSeriesBarcode), divisionMapper)

        every { divisionMapper.mapping(listOf(categoryDepth0, categoryDepth1, categoryDepth2)) } returns
                listOf(categoryDepthCode0, categoryDepthCode1, categoryDepthCode2)

        val result = context.resolveSeriesCode()
        assertThat(result).isEqualTo(secondSeriesBarcode)
    }

    @Test
    fun `resolve series code sBarcode is not null and is not empty`() {
        val context = KyoboBookJsoupDocumentContext(getDocument(isbn, originalBarcode, seriesBarcode, secondSeriesBarcode), divisionMapper)

        every { divisionMapper.mapping(listOf(categoryDepth0, categoryDepth1, categoryDepth2)) } returns
                listOf(categoryDepthCode0, categoryDepthCode1, categoryDepthCode2)

        val result = context.resolveSeriesCode()
        assertThat(result).isEqualTo(seriesBarcode)
    }

    @Test
    fun `resolve series code aBarcode is empty`() {
        val context = KyoboBookJsoupDocumentContext(getDocument(isbn, originalBarcode, null, ""), divisionMapper)

        every { divisionMapper.mapping(listOf(categoryDepth0, categoryDepth1, categoryDepth2)) } returns
                listOf(categoryDepthCode0, categoryDepthCode1, categoryDepthCode2)

        val result = context.resolveSeriesCode()
        assertThat(result).isNull()
    }

    private fun getDocument(isbn: String?, originalBarcode: String?, seriesCode: String?, secondBarcode: String?): Document {
        val document = Document("http://localhost")

        document.appendElement("meta")
            .attr("name", KyoboBookMetaTagNameSelector.author)
            .attr("content", responseAuthor)
            .parent()
            .appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.title)
            .attr("content", title)
            .parent()
            .appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.largeThumbnail)
            .attr("content", largeThumbnail)
            .parent()
            .appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.mediumThumbnail)
            .attr("content", mediumThumbnail)
            .parent()
            .appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.originalPrice)
            .attr("content", originalPrice)
            .parent()
            .appendElement("input")
            .attr("name", KyoboBookInputNameSelector.seriesBarcode)
            .attr("value", seriesCode)
            .parent()
            .appendElement("input")
            .attr("name", KyoboBookInputNameSelector.aBarcode)
            .attr("value", secondBarcode)
            .appendElement("input")
            .attr("name", KyoboBookInputNameSelector.categoryCode)
            .attr("value", responseCategoryCode)
            .parent()
            .appendElement("div")
            .addClass("content_middle")
            .appendElement("div")
            .addClass("content_left")
            .appendElement("div")
            .addClass("box_detail_content")
            .appendElement("div")
            .addClass("box_detail_article")
            .text(description)

        if (isbn != null) {
            document.appendElement("meta")
                .attr("property", KyoboBookMetaTagPropertySelector.isbn)
                .attr("content", isbn)
                .parent()
        }
        if (originalBarcode != null) {
            document.appendElement("meta")
                .attr("property", KyoboBookMetaTagPropertySelector.originalBarcode)
                .attr("content", originalBarcode)
                .parent()
        }

        return document
    }
}