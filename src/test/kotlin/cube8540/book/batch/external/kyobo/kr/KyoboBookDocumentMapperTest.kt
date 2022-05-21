package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.book.domain.DivisionRawMapper
import cube8540.book.batch.interlock.client.InternalBadRequestException
import cube8540.book.batch.interlock.client.InvalidAuthenticationException
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test

class KyoboBookDocumentMapperTest {

    private val divisionRawMapper: DivisionRawMapper = mockk(relaxed = true)

    private val documentMapper = KyoboBookDocumentMapper(divisionRawMapper)

    @Test
    fun `document mapping`() {
        val document = createDocument()

        val result = documentMapper.convertValue(document)
        assertThat(result).isEqualTo(KyoboBookJsoupDocumentContext(document, divisionRawMapper))
    }

    @Test
    fun `document original barcode is null`() {
        val document = createDocument(originalBarcode = null)

        val thrown = catchThrowable { documentMapper.convertValue(document) }
        assertThat(thrown).isInstanceOf(InternalBadRequestException::class.java)
    }

    @Test
    fun `document isbn is null`() {
        val document = createDocument(isbn = null)

        val thrown = catchThrowable { documentMapper.convertValue(document) }
        assertThat(thrown).isInstanceOf(InvalidAuthenticationException::class.java)
    }
}