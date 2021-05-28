package cube8540.book.batch.job.processor

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.BookDetailsFilterFunction
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.OriginalPropertyKey
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class BookDetailsOriginalDataApproveProcessorTest {

    private val processor = BookDetailsOriginalDataApproveProcessor()

    @Test
    fun `filter when original data is not mapped`() {
        val bookDetails: BookDetails = mockk(relaxed = true)
        val original = HashMap<OriginalPropertyKey, String?>()

        original[OriginalPropertyKey("property", MappingType.NATIONAL_LIBRARY)] = "nationalLibraryProperty"
        every { bookDetails.original } returns original

        val result = processor.process(bookDetails)
        assertThat(result).isEqualTo(bookDetails)
    }

    @Test
    fun `filter when original data is mapped and filter result is true`() {
        val mappingType = MappingType.NATIONAL_LIBRARY
        val bookDetails: BookDetails = mockk(relaxed = true)
        val original = HashMap<OriginalPropertyKey, String?>()

        val filterFunction: BookDetailsFilterFunction = mockk(relaxed = true) {
            every { isValid(bookDetails) } returns true
        }

        original[OriginalPropertyKey("property", mappingType)] = "nationalLibraryProperty"
        every { bookDetails.original } returns original

        processor.add(mappingType, filterFunction)

        val result = processor.process(bookDetails)
        assertThat(result).isEqualTo(bookDetails)
    }

    @Test
    fun `filter when original data is mapped and filter result is false`() {
        val mappingType = MappingType.NATIONAL_LIBRARY
        val bookDetails: BookDetails = mockk(relaxed = true)
        val original = HashMap<OriginalPropertyKey, String?>()

        val filterFunction: BookDetailsFilterFunction = mockk(relaxed = true) {
            every { isValid(bookDetails) } returns false
        }

        original[OriginalPropertyKey("property", mappingType)] = "nationalLibraryProperty"
        every { bookDetails.original } returns original

        processor.add(mappingType, filterFunction)

        val result = processor.process(bookDetails)
        assertThat(result).isNull()
    }

    @Test
    fun `filter when any filter result is false`() {
        val bookDetails: BookDetails = mockk(relaxed = true)
        val original = HashMap<OriginalPropertyKey, String?>()

        val filterFunction0: BookDetailsFilterFunction = mockk(relaxed = true) {
            every { isValid(bookDetails) } returns true
        }
        val filterFunction1: BookDetailsFilterFunction = mockk(relaxed = true) {
            every { isValid(bookDetails) } returns false
        }

        original[OriginalPropertyKey("property", MappingType.NATIONAL_LIBRARY)] = "nationalLibraryProperty"
        original[OriginalPropertyKey("property", MappingType.KYOBO)] = "nationalLibraryProperty"
        every { bookDetails.original } returns original

        processor.add(MappingType.NATIONAL_LIBRARY, filterFunction0)
        processor.add(MappingType.KYOBO, filterFunction1)

        val result = processor.process(bookDetails)
        assertThat(result).isNull()
    }

    @AfterEach
    fun cleanup() {
        processor.clear()
    }
}