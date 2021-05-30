package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.book.domain.DivisionRawMapper
import cube8540.book.batch.external.exception.InternalBadRequestException
import cube8540.book.batch.external.exception.InvalidAuthenticationException
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Test

class KyoboBookDocumentMapperTest {

    private val isbn = "9791133447831"
    private val originalBarcode = "og:9791133447831"

    private val divisionRawMapper: DivisionRawMapper = mockk(relaxed = true)

    private val documentMapper = KyoboBookDocumentMapper(divisionRawMapper)

    @Test
    fun `document mapping`() {
        val document = getDocument(isbn, originalBarcode)

        val result = documentMapper.convertValue(document)
        assertThat(result).isEqualTo(KyoboBookJsoupDocumentContext(document, divisionRawMapper))
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