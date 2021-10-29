package cube8540.book.batch.external.aladin.kr

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.domain.OriginalPropertyKey
import cube8540.book.batch.book.domain.createBookDetails
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AladinBookDetailsControllerTest {

    private val controller = AladinBookDetailsController()

    @Nested
    inner class MergeDefaultProperty {

        @Test
        fun `merged base and item`() {
            val original = createBookDetails(isbn = "isbn0000")
            val mergedData = createBookDetails(title = "mergedTitle")

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
                .isEqualToComparingOnlyGivenFields(mergedData, BookDetails::title.name)
        }
    }

    @Nested
    inner class MergeOriginalProperty {

        @Test
        fun `merge when original property is null`() {
            val mergedOriginalMap = HashMap<OriginalPropertyKey, String>()

            mergedOriginalMap[OriginalPropertyKey("mergedProperty", MappingType.ALADIN)] = "mergedValue"

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

            originalMap[OriginalPropertyKey("originalProperty", MappingType.ALADIN)] = "originalValue"
            mergedOriginalMap[OriginalPropertyKey("mergedProperty", MappingType.ALADIN)] = "mergedValue"

            val original = createBookDetails(original = originalMap)
            val mergedData = createBookDetails(original = mergedOriginalMap)

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
            assertThat(result.original).isEqualTo(originalMap + mergedOriginalMap)
        }
    }

}