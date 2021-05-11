package cube8540.book.batch.infra.kyobo.kr

import cube8540.book.batch.domain.DivisionRawMapper
import cube8540.book.batch.infra.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.author
import cube8540.book.batch.infra.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.categoryDepth0
import cube8540.book.batch.infra.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.categoryDepth1
import cube8540.book.batch.infra.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.categoryDepth2
import cube8540.book.batch.infra.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.categoryDepthCode0
import cube8540.book.batch.infra.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.categoryDepthCode1
import cube8540.book.batch.infra.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.categoryDepthCode2
import cube8540.book.batch.infra.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.isbn
import cube8540.book.batch.infra.kyobo.kr.KyoboBookJsoupDocumentContextTestEnvironment.originalBarcode
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Test

class KyoboBookJsoupDocumentContextTest {

    private val document: Document = mockk(relaxed = true)
    private val divisionMapper: DivisionRawMapper = mockk(relaxed = true)

    @Test
    fun `resolve authors`() {
        val context = KyoboBookJsoupDocumentContext(getDocument(isbn, originalBarcode), divisionMapper)

        val result = context.resolveAuthors()
        assertThat(result).isEqualTo(author)
    }

    @Test
    fun `resolve categories`() {
        val context = KyoboBookJsoupDocumentContext(getDocument(isbn, originalBarcode), divisionMapper)

        every { divisionMapper.mapping(listOf(categoryDepth0, categoryDepth1, categoryDepth2)) } returns
                listOf(categoryDepthCode0, categoryDepthCode1, categoryDepthCode2)

        val result = context.resolveDivisions()
        assertThat(result).isEqualTo(setOf(categoryDepthCode0, categoryDepthCode1, categoryDepthCode2))
    }

    private fun getDocument(isbn: String?, originalBarcode: String?): Document {
        val document = Document("http://localhost")

        document.appendElement("meta")
            .attr("name", KyoboBookMetaTagNameSelector.author)
            .attr("content", KyoboBookJsoupDocumentContextTestEnvironment.responseAuthor)
            .parent()
            .appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.title)
            .attr("content", KyoboBookJsoupDocumentContextTestEnvironment.title)
            .parent()
            .appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.largeThumbnail)
            .attr("content", KyoboBookJsoupDocumentContextTestEnvironment.largeThumbnail)
            .parent()
            .appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.mediumThumbnail)
            .attr("content", KyoboBookJsoupDocumentContextTestEnvironment.mediumThumbnail)
            .parent()
            .appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.originalPrice)
            .attr("content", KyoboBookJsoupDocumentContextTestEnvironment.originalPrice)
            .parent()
            .appendElement("input")
            .attr("name", KyoboBookInputNameSelector.seriesBarcode)
            .attr("value", KyoboBookJsoupDocumentContextTestEnvironment.seriesBarcode)
            .parent()
            .appendElement("input")
            .attr("name", KyoboBookInputNameSelector.categoryCode)
            .attr("value", KyoboBookJsoupDocumentContextTestEnvironment.responseCategoryCode)
            .parent()
            .appendElement("div")
            .addClass("content_middle")
            .appendElement("div")
            .addClass("content_left")
            .appendElement("div")
            .addClass("box_detail_content")
            .appendElement("div")
            .addClass("box_detail_article")
            .text(KyoboBookJsoupDocumentContextTestEnvironment.description)

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