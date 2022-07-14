package cube8540.book.batch.translator.aladin.kr

import cube8540.book.batch.book.domain.*
import cube8540.book.batch.translator.aladin.kr.application.AladinBookDetailsController
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.LocalDate

class AladinBookDetailsControllerTest {

    private val controller = AladinBookDetailsController()

    @Nested
    inner class MergeDefaultProperty {

        @Test
        fun `merged base and item`() {
            val mergedPublishDate = LocalDate.of(2021, 11, 14)

            val original = createBookDetails(isbn = "isbn0000")
            val mergedData = createBookDetails(title = "mergedTitle", publishDate = mergedPublishDate)

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
                .isEqualToComparingOnlyGivenFields(mergedData, BookDetails::title.name, BookDetails::publishDate.name)
            assertThat(result.confirmedPublication).isTrue
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

    @Nested
    inner class MergeExternalLink {

        @Test
        fun `merge when original external link is null`() {
            val mergedExternalLink = HashMap<MappingType, BookExternalLink>()

            mergedExternalLink[MappingType.ALADIN] = BookExternalLink(
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

            originalExternalLink[MappingType.KYOBO] = BookExternalLink(
                productDetailPage = URI.create("/"), originalPrice = defaultOriginalPrice, salePrice = defaultSalePrice)
            mergedExternalLink[MappingType.ALADIN] = BookExternalLink(
                productDetailPage = URI.create("/"), originalPrice = defaultOriginalPrice, salePrice = defaultSalePrice)

            val original = createBookDetails(externalLink = originalExternalLink)
            val mergedData = createBookDetails(externalLink = mergedExternalLink)

            val result = controller.merge(original, mergedData)
            assertThat(result).isEqualTo(original)
            assertThat(result.externalLinks).isEqualTo(originalExternalLink + mergedExternalLink)
        }
    }

}