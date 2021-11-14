package cube8540.book.batch.external.naver.com

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.domain.OriginalPropertyKey
import cube8540.book.batch.book.domain.createBookDetails
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.LocalDate

class NaverBookDetailsControllerTest {

    private val controller = NaverBookDetailsController()

    @Nested
    inner class MergeDefaultProperty {

        @Test
        fun `merged base and item`() {
            val original = createBookDetails(isbn = "originalIsbn", title = "originalTitle", publisher = "originalPublisher", publishDate = LocalDate.of(2021, 1, 1))
            val mergedData = createBookDetails(title = "mergedTitle", publisher = "mergedPublisher", publishDate = LocalDate.of(2021, 6, 5))

            val comparingFields = listOf(
                BookDetails::title.name,
                BookDetails::publisher.name,
                BookDetails::publishDate.name
            ).toTypedArray()

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
                .isEqualToComparingOnlyGivenFields(mergedData, *comparingFields)
            assertThat(result.confirmedPublication).isTrue
        }
    }

    @Nested
    inner class MergeThumbnail {

        @Test
        fun `merge when original thumbnail is null`() {
            val mergedSmallThumbnail = URI.create("https://merged-small-thumbnail")

            val original = createBookDetails(isbn = "originalIsbn", largeThumbnail = null, mediumThumbnail = null, smallThumbnail = null)
            val mergedData = createBookDetails(largeThumbnail = null, mediumThumbnail = null, smallThumbnail = mergedSmallThumbnail)

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
            assertThat(result.thumbnail!!.largeThumbnail).isEqualTo(originalLargeThumbnail)
            assertThat(result.thumbnail!!.mediumThumbnail).isEqualTo(originalMediumThumbnail)
            assertThat(result.thumbnail!!.smallThumbnail).isEqualTo(mergedSmallThumbnail)
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