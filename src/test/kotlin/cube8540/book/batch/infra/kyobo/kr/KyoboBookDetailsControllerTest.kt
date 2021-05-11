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
import cube8540.book.batch.infra.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.isbn
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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KyoboBookDetailsControllerTest {

    private val controller = KyoboBookDetailsController()

    @Test
    fun `merge base and item`() {
        val base = BookDetails(isbn)
        val item = BookDetails(isbn)

        item.divisions = mergedDivisions
        item.authors = mergedAuthors
        item.title = mergedTitle
        item.description = mergedDescription
        item.price = mergedPrice

        val result = controller.merge(base, item)
        assertThat(result.title).isEqualTo(mergedTitle)
        assertThat(result.divisions).isEqualTo(mergedDivisions)
        assertThat(result.authors).isEqualTo(mergedAuthors)
        assertThat(result.description).isEqualTo(mergedDescription)
        assertThat(result.price).isEqualTo(mergedPrice)
    }

    @Test
    fun `merged when base thumbnail is null`() {
        val base = BookDetails(isbn)
        val item = BookDetails(isbn)

        item.thumbnail = Thumbnail(mergedLargeThumbnail, mergedMediumThumbnail, mergedSmallThumbnail)

        val result = controller.merge(base, item)
        assertThat(result.thumbnail?.largeThumbnail).isEqualTo(mergedLargeThumbnail)
        assertThat(result.thumbnail?.mediumThumbnail).isEqualTo(mergedMediumThumbnail)
        assertThat(result.thumbnail?.smallThumbnail).isNull()
    }

    @Test
    fun `merged when base thumbnail is not null`() {
        val base = BookDetails(isbn)
        val item = BookDetails(isbn)

        base.thumbnail = Thumbnail(null, null, mergedSmallThumbnail)
        item.thumbnail = Thumbnail(mergedLargeThumbnail, mergedMediumThumbnail, null)

        val result = controller.merge(base, item)
        assertThat(result.thumbnail?.largeThumbnail).isEqualTo(mergedLargeThumbnail)
        assertThat(result.thumbnail?.mediumThumbnail).isEqualTo(mergedMediumThumbnail)
        assertThat(result.thumbnail?.smallThumbnail).isEqualTo(mergedSmallThumbnail)
    }

    @Test
    fun `merged when base original is null`() {
        val base = BookDetails(isbn)
        val item = BookDetails(isbn)
        val itemOriginalProperty = HashMap<OriginalPropertyKey, String>()

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.KYOBO)] = itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.KYOBO)] = itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.KYOBO)] = itemOriginalValue0002

        item.original = itemOriginalProperty

        val result = controller.merge(base, item)
        assertThat(result.original).isEqualTo(itemOriginalProperty)
    }

    @Test
    fun `merged when base original is not null`() {
        val base = BookDetails(isbn)
        val item = BookDetails(isbn)
        val baseOriginalProperty = HashMap<OriginalPropertyKey, String>()
        val itemOriginalProperty = HashMap<OriginalPropertyKey, String>()

        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0000, MappingType.KYOBO)] = existsOriginalValue0000
        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0001, MappingType.KYOBO)] = existsOriginalValue0001
        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0002, MappingType.KYOBO)] = existsOriginalValue0002

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.KYOBO)] = itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.KYOBO)] = itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.KYOBO)] = itemOriginalValue0002

        base.original = baseOriginalProperty
        item.original = itemOriginalProperty

        val result = controller.merge(base, item)
        assertThat(result.original).isEqualTo(baseOriginalProperty + itemOriginalProperty)
    }

}