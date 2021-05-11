package cube8540.book.batch.infra.kyobo.kr

import cube8540.book.batch.domain.DivisionRawMapper
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.OriginalPropertyKey
import cube8540.book.batch.external.exception.InternalBadRequestException
import cube8540.book.batch.external.exception.InvalidAuthenticationException
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.author
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.categoryDepth0
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.categoryDepth1
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.categoryDepth2
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.categoryDepthCode0
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.categoryDepthCode1
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.categoryDepthCode2
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.description
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.isbn
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.largeThumbnail
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.mediumThumbnail
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.originalBarcode
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.originalPrice
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.responseAuthor
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.responseCategoryCode
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.seriesBarcode
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDocumentMapperTestEnvironment.title
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Test
import java.net.URI

class KyoboBookDocumentMapperTest {

    private val divisionRawMapper: DivisionRawMapper = mockk(relaxed = true)

    private val documentMapper = KyoboBookDocumentMapper(divisionRawMapper)

    @Test
    fun `document mapping`() {
        val document = getDocument(isbn, originalBarcode)

        every { divisionRawMapper.mapping(listOf(categoryDepth0, categoryDepth1, categoryDepth2)) } returns
                listOf(categoryDepthCode0, categoryDepthCode1, categoryDepthCode2)

        val result = documentMapper.convertValue(document)
        assertThat(result.isbn).isEqualTo(isbn)
        assertThat(result.title).isEqualTo(title)
        assertThat(result.authors).isEqualTo(author)
        assertThat(result.largeThumbnail).isEqualTo(URI.create(largeThumbnail))
        assertThat(result.mediumThumbnail).isEqualTo(URI.create(mediumThumbnail))
        assertThat(result.price).isEqualTo(originalPrice.toDouble())
        assertThat(result.divisions).isEqualTo(setOf(categoryDepthCode0, categoryDepthCode1, categoryDepthCode2))
        assertThat(result.seriesCode).isEqualTo(seriesBarcode)
        assertThat(result.description).isEqualTo(description)
    }

    @Test
    fun `save original data`() {
        val document = getDocument(isbn, originalBarcode)

        every { divisionRawMapper.mapping(listOf(categoryDepth0, categoryDepth1, categoryDepth2)) } returns
                listOf(categoryDepthCode0, categoryDepthCode1, categoryDepthCode2)

        val result = documentMapper.convertValue(document)
        val original = result.original!!
        assertThat(original[OriginalPropertyKey(KyoboBookMetaTagNameSelector.author, MappingType.KYOBO)]).isEqualTo(responseAuthor)
        assertThat(original[OriginalPropertyKey(KyoboBookMetaTagPropertySelector.title, MappingType.KYOBO)]).isEqualTo(title)
        assertThat(original[OriginalPropertyKey(KyoboBookMetaTagPropertySelector.largeThumbnail, MappingType.KYOBO)]).isEqualTo(largeThumbnail)
        assertThat(original[OriginalPropertyKey(KyoboBookMetaTagPropertySelector.mediumThumbnail, MappingType.KYOBO)]).isEqualTo(mediumThumbnail)
        assertThat(original[OriginalPropertyKey(KyoboBookMetaTagPropertySelector.originalPrice, MappingType.KYOBO)]).isEqualTo(originalPrice)
        assertThat(original[OriginalPropertyKey(KyoboBookInputNameSelector.seriesBarcode, MappingType.KYOBO)]).isEqualTo(seriesBarcode)
        assertThat(original[OriginalPropertyKey(KyoboBookInputNameSelector.categoryCode, MappingType.KYOBO)]).isEqualTo(responseCategoryCode)
    }

    @Test
    fun `document original barcode is null`() {
        val document = getDocument(isbn, null)

        val thrown = catchThrowable { documentMapper.convertValue(document) }
        assertThat(thrown).isInstanceOf(InternalBadRequestException::class.java)
    }

    @Test
    fun `document isbn is null`() {
        val document = getDocument(null, originalBarcode)

        val thrown = catchThrowable { documentMapper.convertValue(document) }
        assertThat(thrown).isInstanceOf(InvalidAuthenticationException::class.java)
    }

    private fun getDocument(isbn: String?, originalBarcode: String?): Document {
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
                .attr("value", seriesBarcode)
                .parent()
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