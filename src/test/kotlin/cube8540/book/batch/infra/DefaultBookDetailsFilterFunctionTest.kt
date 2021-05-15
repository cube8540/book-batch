package cube8540.book.batch.infra

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.BookOriginalFilter
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.repository.BookOriginalFilterRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultBookDetailsFilterFunctionTest {

    private val repository: BookOriginalFilterRepository = mockk(relaxed = true)

    @Test
    fun initialization() {
        val mappingType: MappingType = mockk(relaxed = true)
        val filter: BookOriginalFilter = mockk(relaxed = true)

        every { repository.findRootByMappingType(mappingType) } returns filter

        val filterFunction = DefaultBookDetailsFilterFunction(mappingType, repository)
        assertThat(filterFunction.cache).isEqualTo(filter)
    }

    @Test
    fun `filtering book details`() {
        val bookDetails: BookDetails = mockk(relaxed = true)
        val mappingType: MappingType = mockk(relaxed = true)
        val filter: BookOriginalFilter = mockk(relaxed = true) {
            every { isValid(bookDetails) } returns true
        }

        every { repository.findRootByMappingType(mappingType) } returns filter

        val filterFunction = DefaultBookDetailsFilterFunction(mappingType, repository)
        val result = filterFunction.isValid(bookDetails)
        assertThat(result).isTrue
    }

    @Test
    fun `filtering cache is null`() {
        val bookDetails: BookDetails = mockk(relaxed = true)
        val mappingType: MappingType = mockk(relaxed = true)

        every { repository.findRootByMappingType(mappingType) } returns null

        val filterFunction = DefaultBookDetailsFilterFunction(mappingType, repository)
        val result = filterFunction.isValid(bookDetails)
        assertThat(result).isTrue
    }

}