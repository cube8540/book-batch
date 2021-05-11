package cube8540.book.batch.infra.naver.com

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.OriginalPropertyKey
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.existsOriginalProperty0000
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.existsOriginalProperty0001
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.existsOriginalProperty0002
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.existsOriginalValue0000
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.existsOriginalValue0001
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.existsOriginalValue0002
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.isbn
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.itemOriginalProperty0000
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.itemOriginalProperty0001
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.itemOriginalProperty0002
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.itemOriginalValue0000
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.itemOriginalValue0001
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.itemOriginalValue0002
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.mergedPublishDate
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.mergedPublisher
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.mergedSmallThumbnail
import cube8540.book.batch.infra.naver.com.NaverBookDetailsControllerTestEnvironment.mergedTitle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NaverBookDetailsControllerTest {

    private val controller = NaverBookDetailsController()

    @Test
    fun `merged when base original is null`() {
        val base = BookDetails(isbn)
        val item = BookDetails(isbn)
        val itemOriginalProperty = HashMap<OriginalPropertyKey, String>()

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.NATIONAL_LIBRARY)] = itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.NATIONAL_LIBRARY)] = itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.NATIONAL_LIBRARY)] =  itemOriginalValue0002

        item.original = itemOriginalProperty
        item.title = mergedTitle
        item.publisher = mergedPublisher
        item.publishDate = mergedPublishDate
        item.smallThumbnail = mergedSmallThumbnail

        val result = controller.merge(base, item)
        assertThat(result.title).isEqualTo(mergedTitle)
        assertThat(result.publisher).isEqualTo(mergedPublisher)
        assertThat(result.smallThumbnail).isEqualTo(mergedSmallThumbnail)
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

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.NATIONAL_LIBRARY)] =
            itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.NATIONAL_LIBRARY)] =
            itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.NATIONAL_LIBRARY,)] =
            itemOriginalValue0002

        base.original = baseOriginalProperty
        item.original = itemOriginalProperty
        item.title = mergedTitle
        item.publisher = mergedPublisher
        item.publishDate = mergedPublishDate
        item.smallThumbnail = mergedSmallThumbnail

        val result = controller.merge(base, item)
        assertThat(result.title).isEqualTo(mergedTitle)
        assertThat(result.publisher).isEqualTo(mergedPublisher)
        assertThat(result.smallThumbnail).isEqualTo(mergedSmallThumbnail)
        assertThat(result.original).isEqualTo(baseOriginalProperty + itemOriginalProperty)
    }

}