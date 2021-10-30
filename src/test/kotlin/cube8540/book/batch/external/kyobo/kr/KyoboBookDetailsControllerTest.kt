package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.domain.OriginalPropertyKey
import cube8540.book.batch.book.domain.createBookDetails
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI

class KyoboBookDetailsControllerTest {

    private val controller = KyoboBookDetailsController()

    @Nested
    inner class MergedDefaultProperty {

        @Test
        fun `merge base and item`() {
            val mergedAuthors = setOf("mergedAuthors")
            val mergedDivision = setOf("mergedAuthors")
            val mergedIndex = listOf("mergedIndex0000")

            val original = createBookDetails(isbn = "isbn0000")
            val mergedData = createBookDetails(title = "mergedTitle", seriesCode = "mergedSeriesCode0000", authors = mergedAuthors, divisions = mergedDivision, description = "mergedDescription", index = mergedIndex)

            val comparingFields = listOf(
                BookDetails::seriesCode.name,
                BookDetails::title.name,
                BookDetails::authors.name,
                BookDetails::divisions.name,
                BookDetails::indexes.name
            ).toTypedArray()

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
                .isEqualToComparingOnlyGivenFields(mergedData, *comparingFields)
        }
    }
    @Nested
    inner class MergeThumbnail {

        @Test
        fun `merge when original thumbnail is null`() {
            val mergedLargeThumbnail = URI.create("https://merged-large-thumbnail")
            val mergedMediumThumbnail = URI.create("https://merged-medium-thumbnail")

            val original = createBookDetails(isbn = "originalIsbn", largeThumbnail = null, mediumThumbnail = null, smallThumbnail = null)
            val mergedData = createBookDetails(largeThumbnail = mergedLargeThumbnail, mediumThumbnail = mergedMediumThumbnail, smallThumbnail = null)

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
                .isEqualToComparingOnlyGivenFields(mergedData, "thumbnail")
        }

        @Test
        fun `merge when original thumbnail is not null`() {
            val originalLargeThumbnail = URI.create("https://original-large-thumbnail")
            val originalMediumThumbnail = URI.create("https://original-medium-thumbnail")
            val originalSmallThumbnail = URI.create("https://original-small-thumbnail")

            val mergedLargeThumbnail = URI.create("https://merged-large-thumbnail")
            val mergedMediumThumbnail = URI.create("https://merged-medium-thumbnail")
            val mergedSmallThumbnail = URI.create("https://merged-small-thumbnail")

            val original = createBookDetails(isbn = "originalIsbn", largeThumbnail = originalLargeThumbnail, mediumThumbnail = originalMediumThumbnail, smallThumbnail = originalSmallThumbnail)
            val mergedData = createBookDetails(largeThumbnail = mergedLargeThumbnail, mediumThumbnail = mergedMediumThumbnail, smallThumbnail = mergedSmallThumbnail)

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
            assertThat(result.thumbnail!!.largeThumbnail).isEqualTo(mergedLargeThumbnail)
            assertThat(result.thumbnail!!.mediumThumbnail).isEqualTo(mergedMediumThumbnail)
            assertThat(result.thumbnail!!.smallThumbnail).isEqualTo(originalSmallThumbnail)
        }
    }

    @Nested
    inner class MergeOriginalProperty {

        @Test
        fun `merge when original property is null`() {
            val mergedOriginalMap = HashMap<OriginalPropertyKey, String>()

            mergedOriginalMap[OriginalPropertyKey("mergedProperty", MappingType.NAVER_BOOK)] = "mergedValue"

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

            originalMap[OriginalPropertyKey("originalProperty", MappingType.NAVER_BOOK)] = "originalValue"
            mergedOriginalMap[OriginalPropertyKey("mergedProperty", MappingType.NAVER_BOOK)] = "mergedValue"

            val original = createBookDetails(original = originalMap)
            val mergedData = createBookDetails(original = mergedOriginalMap)

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
            assertThat(result.original).isEqualTo(originalMap + mergedOriginalMap)
        }
    }
}