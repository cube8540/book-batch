package cube8540.book.batch.job.processor

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.job.processor.BookDetailsFilterProcessor
import io.github.cube8540.validator.core.Validatable
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BookDetailsFilterProcessorTest {

    private val validator: Validatable<BookDetails> = mockk(relaxed = true)

    private val processor = BookDetailsFilterProcessor(validator)

    @Test
    fun `filtering processing when validator returns true`() {
        val booKDetails: BookDetails = mockk(relaxed = true)

        every { validator.isValid(booKDetails) } returns true

        val result = processor.process(booKDetails)
        assertThat(result).isEqualTo(booKDetails)
    }

    @Test
    fun `filtering processing when validator returns false`() {
        val bookDetails: BookDetails = mockk(relaxed = true)

        every { validator.isValid(bookDetails) } returns false

        val result = processor.process(bookDetails)
        assertThat(result).isNull()
    }
}