package cube8540.book.batch.infra

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.BookOriginalFilter
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.repository.BookOriginalFilterRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultBookDetailsFilterFunctionTest {

    private val mappingType: MappingType = mockk(relaxed = true)
    private val repository: BookOriginalFilterRepository = mockk(relaxed = true)

    private val filterFunction = DefaultBookDetailsFilterFunction(mappingType, repository)

    @Test
    fun initialization() {
        val filter: BookOriginalFilter = mockk(relaxed = true)

        every { repository.findRootByMappingType(mappingType) } returns filter
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

        every { repository.findRootByMappingType(mappingType) } returns filter
        filterFunction.afterPropertiesSet()

        val result = filterFunction.isValid(bookDetails)
        assertThat(result).isTrue
    }

    @Test
    fun `filtering cache is null`() {
        val bookDetails: BookDetails = mockk(relaxed = true)

        every { repository.findRootByMappingType(mappingType) } returns null
        filterFunction.afterPropertiesSet()

        val result = filterFunction.isValid(bookDetails)
        assertThat(result).isTrue
    }

}