package cube8540.book.batch.external.nl.go

import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.domain.OriginalPropertyKey
import cube8540.book.batch.book.domain.createBookDetails
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NationalLibraryBookDetailsControllerTest {

    private val controller = NationalLibraryBookDetailsController()

    @Nested
    inner class MergeDefaultProperty {

        @Test
        fun `merged base and item`() {
            val original = createBookDetails(isbn = "isbn0000")
            val mergedData = createBookDetails(seriesIsbn = "mergedSeriesIsbn00001")

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
                .isEqualToComparingOnlyGivenFields(mergedData, "seriesIsbn")
        }
    }

    @Nested
    inner class MergeOriginalProperty {

        @Test
        fun `merge when original property is null`() {
            val mergedOriginalMap = HashMap<OriginalPropertyKey, String>()

            mergedOriginalMap[OriginalPropertyKey("mergedProperty", MappingType.NATIONAL_LIBRARY)] = "mergedValue"

            val original = createBookDetails(original = null)
            val mergedData = createBookDetails(original = mergedOriginalMap)

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
            assertThat(result.original).isEqualTo(mergedOriginalMap)
        }

        @Test
        fun `merge when original property is not null`() {
            val originalMap = HashMap<OriginalPropertyKey, String>()
            val mergedOriginalMap = HashMap<OriginalPropertyKey, String>()

            originalMap[OriginalPropertyKey("originalProperty", MappingType.NATIONAL_LIBRARY)] = "originalValue"
            mergedOriginalMap[OriginalPropertyKey("mergedProperty", MappingType.NATIONAL_LIBRARY)] = "mergedValue"

            val original = createBookDetails(original = originalMap)
            val mergedData = createBookDetails(original = mergedOriginalMap)

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
            assertThat(result.original).isEqualTo(originalMap + mergedOriginalMap)
        }
    }
}