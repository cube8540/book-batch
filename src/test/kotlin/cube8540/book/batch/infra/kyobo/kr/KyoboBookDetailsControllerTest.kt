package cube8540.book.batch.infra.kyobo.kr

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.OriginalPropertyKey
import cube8540.book.batch.domain.Thumbnail
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.existsOriginalProperty0000
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.existsOriginalProperty0001
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.existsOriginalProperty0002
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.existsOriginalValue0000
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.existsOriginalValue0001
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.existsOriginalValue0002
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.itemOriginalProperty0000
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.itemOriginalProperty0001
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.itemOriginalProperty0002
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.itemOriginalValue0000
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.itemOriginalValue0001
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.itemOriginalValue0002
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedAuthors
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedDescription
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedDivisions
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedLargeThumbnail
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedMediumThumbnail
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedPrice
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedSmallThumbnail
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedTitle
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KyoboBookDetailsControllerTest {

    private val controller = KyoboBookDetailsController()

    @Test
    fun `merge base and item`() {
        val base: BookDetails = mockk(relaxed = true)
        val item: BookDetails = mockk(relaxed = true) {
            every { divisions } returns mergedDivisions
            every { authors } returns mergedAuthors
            every { title } returns mergedTitle
            every { description } returns mergedDescription
            every { price } returns mergedPrice
        }

        val result = controller.merge(base, item)
        assertThat(result).isEqualTo(base)
        verify {
            base.divisions = mergedDivisions
            base.authors = mergedAuthors
            base.title = mergedTitle
            base.description = mergedDescription
            base.price = mergedPrice
        }
    }

    @Test
    fun `merged when base thumbnail is null`() {
        val base: BookDetails = mockk(relaxed = true) {
            every { thumbnail } returns null
        }
        val item: BookDetails = mockk(relaxed = true) {
            every { thumbnail } returns Thumbnail(mergedLargeThumbnail, mergedMediumThumbnail, mergedSmallThumbnail)
        }

        val result = controller.merge(base, item)
        assertThat(result).isEqualTo(base)
        verify { base.thumbnail = Thumbnail(mergedLargeThumbnail, mergedMediumThumbnail, null) }
    }

    @Test
    fun `merged when base thumbnail is not null`() {
        val baseThumbnail = Thumbnail(null, null, mergedSmallThumbnail)
        val base: BookDetails = mockk(relaxed = true) {
            every { thumbnail } returns baseThumbnail
        }
        val item: BookDetails = mockk(relaxed = true) {
            every { thumbnail } returns Thumbnail(mergedLargeThumbnail, mergedMediumThumbnail, null)
        }

        val result = controller.merge(base, item)
        assertThat(result).isEqualTo(base)
        assertThat(baseThumbnail.largeThumbnail).isEqualTo(mergedLargeThumbnail)
        assertThat(baseThumbnail.mediumThumbnail).isEqualTo(mergedMediumThumbnail)
        assertThat(baseThumbnail.smallThumbnail).isEqualTo(mergedSmallThumbnail)
    }

    @Test
    fun `merged when base original is null`() {
        val base: BookDetails = mockk(relaxed = true)
        val item: BookDetails = mockk(relaxed = true)
        val itemOriginalProperty = HashMap<OriginalPropertyKey, String>()

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.KYOBO)] = itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.KYOBO)] = itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.KYOBO)] = itemOriginalValue0002
        every { item.original } returns itemOriginalProperty

        item.original = itemOriginalProperty

        val result = controller.merge(base, item)
        assertThat(result).isEqualTo(base)
        verify { base.original = itemOriginalProperty }
    }

    @Test
    fun `merged when base original is not null`() {
        val baseOriginalProperty = HashMap<OriginalPropertyKey, String>()
        val itemOriginalProperty = HashMap<OriginalPropertyKey, String>()

        val base: BookDetails = mockk(relaxed = true) {
            every { original } returns baseOriginalProperty
        }
        val item: BookDetails = mockk(relaxed = true) {
            every { original } returns itemOriginalProperty
        }

        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0000, MappingType.KYOBO)] = existsOriginalValue0000
        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0001, MappingType.KYOBO)] = existsOriginalValue0001
        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0002, MappingType.KYOBO)] = existsOriginalValue0002

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.KYOBO)] = itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.KYOBO)] = itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.KYOBO)] = itemOriginalValue0002

        val result = controller.merge(base, item)
        assertThat(result).isEqualTo(base)
        verify { base.original = baseOriginalProperty + itemOriginalProperty }
    }
}