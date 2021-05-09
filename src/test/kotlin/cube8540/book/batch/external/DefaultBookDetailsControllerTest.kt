package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.OriginalPropertyKey
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.existsOriginalProperty0000
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.existsOriginalProperty0001
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.existsOriginalProperty0002
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.existsOriginalValue0000
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.existsOriginalValue0001
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.existsOriginalValue0002
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.isbn
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.itemOriginalProperty0000
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.itemOriginalProperty0001
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.itemOriginalProperty0002
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.itemOriginalValue0000
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.itemOriginalValue0001
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.itemOriginalValue0002
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.mergedDescription
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.mergedLargeThumbnail
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.mergedMediumThumbnail
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.mergedPrice
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.mergedPublishDate
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.mergedPublisher
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.mergedSmallThumbnail
import cube8540.book.batch.external.DefaultBookDetailsControllerTestEnvironment.mergedTitle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultBookDetailsControllerTest {

    private val controller = DefaultBookDetailsController()

    @Test
    fun `merged when base original is null`() {
        val base = BookDetails(isbn)
        val item = BookDetails(isbn)
        val itemOriginalProperty = HashMap<OriginalPropertyKey, String>()

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.NATIONAL_LIBRARY)] = itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.NATIONAL_LIBRARY)] = itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.NATIONAL_LIBRARY,)] = itemOriginalValue0002

        item.original = itemOriginalProperty
        item.title = mergedTitle
        item.publisher = mergedPublisher
        item.publishDate = mergedPublishDate
        item.largeThumbnail = mergedLargeThumbnail
        item.mediumThumbnail = mergedMediumThumbnail
        item.smallThumbnail = mergedSmallThumbnail
        item.description = mergedDescription
        item.price = mergedPrice

        val result = controller.merge(base, item)
        assertThat(result.title).isEqualTo(mergedTitle)
        assertThat(result.publisher).isEqualTo(mergedPublisher)
        assertThat(result.largeThumbnail).isEqualTo(mergedLargeThumbnail)
        assertThat(result.mediumThumbnail).isEqualTo(mergedMediumThumbnail)
        assertThat(result.smallThumbnail).isEqualTo(mergedSmallThumbnail)
        assertThat(result.description).isEqualTo(mergedDescription)
        assertThat(result.price).isEqualTo(mergedPrice)
        assertThat(result.original).isEqualTo(itemOriginalProperty)
    }

    @Test
    fun `merged when base original is not null`() {
        val base = BookDetails(isbn)
        val item = BookDetails(isbn)
        val baseOriginalProperty = HashMap<OriginalPropertyKey, String>()
        val itemOriginalProperty = HashMap<OriginalPropertyKey, String>()

        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0000, MappingType.NAVER_BOOK)] = existsOriginalValue0000
        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0001, MappingType.NAVER_BOOK)] = existsOriginalValue0001
        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0002, MappingType.NAVER_BOOK)] = existsOriginalValue0002

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.NATIONAL_LIBRARY)] = itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.NATIONAL_LIBRARY)] = itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.NATIONAL_LIBRARY,)] = itemOriginalValue0002

        base.original = baseOriginalProperty
        item.original = itemOriginalProperty
        item.title = mergedTitle
        item.publisher = mergedPublisher
        item.publishDate = mergedPublishDate
        item.largeThumbnail = mergedLargeThumbnail
        item.mediumThumbnail = mergedMediumThumbnail
        item.smallThumbnail = mergedSmallThumbnail
        item.description = mergedDescription
        item.price = mergedPrice

        val result = controller.merge(base, item)
        assertThat(result.title).isEqualTo(mergedTitle)
        assertThat(result.publisher).isEqualTo(mergedPublisher)
        assertThat(result.largeThumbnail).isEqualTo(mergedLargeThumbnail)
        assertThat(result.mediumThumbnail).isEqualTo(mergedMediumThumbnail)
        assertThat(result.smallThumbnail).isEqualTo(mergedSmallThumbnail)
        assertThat(result.description).isEqualTo(mergedDescription)
        assertThat(result.price).isEqualTo(mergedPrice)
        assertThat(result.original).isEqualTo(baseOriginalProperty + itemOriginalProperty)
    }
}