package cube8540.book.batch.external.nl.go

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.OriginalPropertyKey
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.existsOriginalProperty0000
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.existsOriginalProperty0001
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.existsOriginalProperty0002
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.existsOriginalValue0000
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.existsOriginalValue0001
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.existsOriginalValue0002
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.itemOriginalProperty0000
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.itemOriginalProperty0001
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.itemOriginalProperty0002
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.itemOriginalValue0000
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.itemOriginalValue0001
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.itemOriginalValue0002
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsControllerTestEnvironment.seriesIsbn
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class NationalLibraryBookDetailsControllerTest {

    private val controller = NationalLibraryBookDetailsController()

    @Test
    fun `merge base and item`() {
        val base: BookDetails = mockk(relaxed = true)
        val item: BookDetails = mockk(relaxed = true)

        every { item.seriesIsbn } returns seriesIsbn

        val result = controller.merge(base, item)
        Assertions.assertThat(result).isEqualTo(base)
        verify {
            base.seriesIsbn = seriesIsbn
        }
    }

    @Test
    fun `merged when base original is null`() {
        val itemOriginalProperty = HashMap<OriginalPropertyKey, String?>()

        val base: BookDetails = mockk(relaxed = true)
        val item: BookDetails = mockk(relaxed = true) {
            every { original } returns itemOriginalProperty
        }

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.NATIONAL_LIBRARY)] = itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.NATIONAL_LIBRARY)] = itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.NATIONAL_LIBRARY)] = itemOriginalValue0002

        item.original = itemOriginalProperty

        val result = controller.merge(base, item)
        Assertions.assertThat(result).isEqualTo(base)
        verify { result.original = itemOriginalProperty }
    }

    @Test
    fun `merged when base original is not null`() {
        val baseOriginalProperty = HashMap<OriginalPropertyKey, String?>()
        val itemOriginalProperty = HashMap<OriginalPropertyKey, String?>()

        val base: BookDetails = mockk(relaxed = true) {
            every { original } returns baseOriginalProperty
        }
        val item: BookDetails = mockk(relaxed = true) {
            every { original } returns itemOriginalProperty
        }

        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0000, MappingType.NATIONAL_LIBRARY)] = existsOriginalValue0000
        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0001, MappingType.NATIONAL_LIBRARY)] = existsOriginalValue0001
        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0002, MappingType.NATIONAL_LIBRARY)] = existsOriginalValue0002

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.NAVER_BOOK)] = itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.NAVER_BOOK)] = itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.NAVER_BOOK)] = itemOriginalValue0002

        val result = controller.merge(base, item)
        Assertions.assertThat(result).isEqualTo(base)
        verify { result.original = (baseOriginalProperty + itemOriginalProperty).toMutableMap() }
    }

}