package cube8540.book.batch.translator.kyobo.kr.application

import cube8540.book.batch.book.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.LocalDate

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
            val mergedData = createBookDetails(title = "mergedTitle", seriesCode = "mergedSeriesCode0000",
                authors = mergedAuthors, divisions = mergedDivision, description = "mergedDescription", index = mergedIndex)

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
            assertThat(result.confirmedPublication).isTrue
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

    @Nested
    inner class MergeExternalLink {

        @Test
        fun `merge when original external link is null`() {
            val mergedExternalLink = HashMap<MappingType, BookExternalLink>()

            mergedExternalLink[MappingType.KYOBO] = BookExternalLink(
                productDetailPage = URI.create("/"), originalPrice = defaultOriginalPrice, salePrice = defaultSalePrice)

            val original = createBookDetails(externalLink = null)
            val mergedData = createBookDetails(externalLink = mergedExternalLink)

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
            assertThat(result.externalLinks).isEqualTo(mergedExternalLink)
        }

        @Test
        fun `merge when original external link is not null`() {
            val originalExternalLink = HashMap<MappingType, BookExternalLink>()
            val mergedExternalLink = HashMap<MappingType, BookExternalLink>()

            originalExternalLink[MappingType.ALADIN] = BookExternalLink(
                productDetailPage = URI.create("/"), originalPrice = defaultOriginalPrice, salePrice = defaultSalePrice)
            mergedExternalLink[MappingType.KYOBO] = BookExternalLink(
                productDetailPage = URI.create("/"), originalPrice = defaultOriginalPrice, salePrice = defaultSalePrice)

            val original = createBookDetails(externalLink = originalExternalLink)
            val mergedData = createBookDetails(externalLink = mergedExternalLink)

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
            assertThat(result.externalLinks).isEqualTo(originalExternalLink + mergedExternalLink)
        }
    }

    @Nested
    inner class MergedPublishDate {

        @Test
        fun `merge when publish date is null`() {
            val original = createBookDetails(publishDate = cube8540.book.batch.translator.kyobo.kr.defaultPublishDate)
            val mergedData = createBookDetails(publishDate = null)

            val result = controller.merge(original, mergedData)
            assertThat(result.publishDate).isEqualTo(cube8540.book.batch.translator.kyobo.kr.defaultPublishDate)
        }

        @Test
        fun `merge when publish date is not null`() {
            val mergedPublishDate = LocalDate.of(2021, 11, 14)

            val original = createBookDetails()
            val mergedData = createBookDetails(publishDate = mergedPublishDate)

            val result = controller.merge(original, mergedData)
            assertThat(result.publishDate).isEqualTo(mergedPublishDate)
        }
    }
}