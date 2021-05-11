package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.OriginalPropertyKey
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BookOriginalPropertyRegexFilterTest {

    private val originalPropertyKey: OriginalPropertyKey = mockk(relaxed = true)
    private val regex: Regex = mockk(relaxed = true)

    private val filter = BookOriginalPropertyRegexFilter(originalPropertyKey, regex)

    @Test
    fun `filtering by regex`() {
        val propertyValues = "propertyValues0000"

        val bookDetails: BookDetails = mockk(relaxed = true) {
            every { original } returns mockk(relaxed = true) {
                every { get(originalPropertyKey) } returns propertyValues
            }
        }
        every { regex.matches(propertyValues) } returns true

        val result = filter.isValid(bookDetails)
        assertThat(result).isTrue
    }

}