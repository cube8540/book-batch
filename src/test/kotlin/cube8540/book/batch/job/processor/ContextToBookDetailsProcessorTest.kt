package cube8540.book.batch.job.processor

import cube8540.book.batch.book.domain.BookDetailsContext
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContextToBookDetailsProcessorTest {

    private val processor = ContextToBookDetailsProcessor()

    @Test
    fun `to book details`() {
        val context: BookDetailsContext = mockk(relaxed = true)

        every { context.resolveIsbn() } returns "isbn0000"

        val result = processor.process(context)
        assertThat(result.isbn).isEqualTo("isbn0000")
    }
}