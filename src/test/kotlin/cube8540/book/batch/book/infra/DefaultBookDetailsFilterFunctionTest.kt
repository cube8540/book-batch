package cube8540.book.batch.book.infra

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookOriginalFilter
import cube8540.book.batch.book.domain.defaultMappingType
import cube8540.book.batch.book.repository.BookOriginalFilterRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultBookDetailsFilterFunctionTest {

    private val repository: BookOriginalFilterRepository = mockk(relaxed = true)

    private val filterFunction = DefaultBookDetailsFilterFunction(defaultMappingType, repository)

    @Test
    fun initialization() {
        val filter: BookOriginalFilter = mockk(relaxed = true)

        every { repository.findRootByMappingType(defaultMappingType) } returns filter
        filterFunction.afterPropertiesSet()

        assertThat(filterFunction.cache).isEqualTo(filter)
        verify { repository.detached(filterFunction.cache!!) }
    }

    @Test
    fun `filtering book details`() {
        val bookDetails: BookDetails = mockk(relaxed = true)
        val filter: BookOriginalFilter = mockk(relaxed = true) {
            every { isValid(bookDetails) } returns true
        }

        every { repository.findRootByMappingType(defaultMappingType) } returns filter
        filterFunction.afterPropertiesSet()

        val result = filterFunction.isValid(bookDetails)
        assertThat(result).isTrue
    }

    @Test
    fun `filtering cache is null`() {
        val bookDetails: BookDetails = mockk(relaxed = true)

        every { repository.findRootByMappingType(defaultMappingType) } returns null
        filterFunction.afterPropertiesSet()

        val result = filterFunction.isValid(bookDetails)
        assertThat(result).isTrue
    }

}