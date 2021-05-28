package cube8540.book.batch.external.naver.com

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.OriginalPropertyKey
import cube8540.book.batch.domain.Thumbnail
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.existsOriginalProperty0000
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.existsOriginalProperty0001
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.existsOriginalProperty0002
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.existsOriginalValue0000
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.existsOriginalValue0001
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.existsOriginalValue0002
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.itemOriginalProperty0000
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.itemOriginalProperty0001
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.itemOriginalProperty0002
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.itemOriginalValue0000
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.itemOriginalValue0001
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.itemOriginalValue0002
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.mergedLargeThumbnail
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.mergedMediumThumbnail
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.mergedPublishDate
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.mergedPublisher
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.mergedSmallThumbnail
import cube8540.book.batch.external.naver.com.NaverBookDetailsControllerTestEnvironment.mergedTitle
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NaverBookDetailsControllerTest {

    private val controller = NaverBookDetailsController()

    @Test
    fun `merge base and item`() {
        val base: BookDetails = mockk(relaxed = true)
        val item: BookDetails = mockk(relaxed = true) {
            every { title } returns mergedTitle
            every { publisher } returns mergedPublisher
            every { publishDate } returns mergedPublishDate
        }

        val result = controller.merge(base, item)
        assertThat(result).isEqualTo(base)
        verify {
            base.title = mergedTitle
            base.publisher = mergedPublisher
            base.publishDate = mergedPublishDate
        }
    }

    @Test
    fun `merge when base thumbnail is null`() {
        val base: BookDetails = mockk(relaxed = true) {
            every { thumbnail } returns null
        }
        val item: BookDetails = mockk(relaxed = true) {
            every { thumbnail } returns Thumbnail(mergedLargeThumbnail, mergedMediumThumbnail, mergedSmallThumbnail)
        }

        val result = controller.merge(base, item)
        assertThat(result).isEqualTo(base)
        verify { result.thumbnail = Thumbnail(null, null, mergedSmallThumbnail) }

    }

    @Test
    fun `merge when base thumbnail is not null`() {
        val baseThumbnail = Thumbnail(mergedLargeThumbnail, mergedMediumThumbnail, null)
        val itemThumbnail = Thumbnail(null, null, mergedSmallThumbnail)
        val base: BookDetails = mockk(relaxed = true) {
            every { thumbnail } returns baseThumbnail
        }
        val item: BookDetails = mockk(relaxed = true) {
            every { thumbnail } returns itemThumbnail
        }

        val result = controller.merge(base, item)
        assertThat(result).isEqualTo(base)
        assertThat(baseThumbnail.smallThumbnail).isEqualTo(mergedSmallThumbnail)
        assertThat(baseThumbnail.largeThumbnail).isEqualTo(mergedLargeThumbnail)
        assertThat(baseThumbnail.mediumThumbnail).isEqualTo(mergedMediumThumbnail)
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
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.NATIONAL_LIBRARY)] =  itemOriginalValue0002

        item.original = itemOriginalProperty

        val result = controller.merge(base, item)
        assertThat(result).isEqualTo(base)
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

        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0000, MappingType.NAVER_BOOK)] = existsOriginalValue0000
        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0001, MappingType.NAVER_BOOK)] = existsOriginalValue0001
        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0002, MappingType.NAVER_BOOK)] = existsOriginalValue0002

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.NATIONAL_LIBRARY)] =
            itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.NATIONAL_LIBRARY)] =
            itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.NATIONAL_LIBRARY,)] =
            itemOriginalValue0002

        val result = controller.merge(base, item)
        assertThat(result).isEqualTo(base)
        verify { result.original = (baseOriginalProperty + itemOriginalProperty).toMutableMap() }
    }

}