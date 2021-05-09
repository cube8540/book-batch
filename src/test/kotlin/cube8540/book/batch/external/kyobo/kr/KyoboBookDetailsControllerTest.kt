package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.OriginalPropertyKey
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.existsOriginalProperty0000
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.existsOriginalProperty0001
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.existsOriginalProperty0002
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.existsOriginalValue0000
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.existsOriginalValue0001
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.existsOriginalValue0002
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.isbn
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.itemOriginalProperty0000
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.itemOriginalProperty0001
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.itemOriginalProperty0002
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.itemOriginalValue0000
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.itemOriginalValue0001
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.itemOriginalValue0002
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedAuthors
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedDescription
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedDivisions
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedLargeThumbnail
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedMediumThumbnail
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedPrice
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.mergedTitle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KyoboBookDetailsControllerTest {

    private val controller = KyoboBookDetailsController()

    @Test
    fun `merged when base original is null`() {
        val base = BookDetails(isbn)
        val item = BookDetails(isbn)
        val itemOriginalProperty = HashMap<OriginalPropertyKey, String>()

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.KYOBO)] = itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.KYOBO)] = itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.KYOBO)] = itemOriginalValue0002

        item.divisions = mergedDivisions
        item.authors = mergedAuthors
        item.title = mergedTitle
        item.largeThumbnail = mergedLargeThumbnail
        item.mediumThumbnail = mergedMediumThumbnail
        item.description = mergedDescription
        item.price = mergedPrice
        item.original = itemOriginalProperty

        val result = controller.merge(base, item)
        assertThat(result.title).isEqualTo(mergedTitle)
        assertThat(result.divisions).isEqualTo(mergedDivisions)
        assertThat(result.authors).isEqualTo(mergedAuthors)
        assertThat(result.largeThumbnail).isEqualTo(mergedLargeThumbnail)
        assertThat(result.mediumThumbnail).isEqualTo(mergedMediumThumbnail)
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

        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0000, MappingType.KYOBO)] = existsOriginalValue0000
        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0001, MappingType.KYOBO)] = existsOriginalValue0001
        baseOriginalProperty[OriginalPropertyKey(existsOriginalProperty0002, MappingType.KYOBO)] = existsOriginalValue0002

        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0000, MappingType.KYOBO)] = itemOriginalValue0000
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0001, MappingType.KYOBO)] = itemOriginalValue0001
        itemOriginalProperty[OriginalPropertyKey(itemOriginalProperty0002, MappingType.KYOBO)] = itemOriginalValue0002

        item.divisions = mergedDivisions
        item.authors = mergedAuthors
        item.title = mergedTitle
        item.largeThumbnail = mergedLargeThumbnail
        item.mediumThumbnail = mergedMediumThumbnail
        item.description = mergedDescription
        item.price = mergedPrice

        base.original = baseOriginalProperty
        item.original = itemOriginalProperty

        val result = controller.merge(base, item)
        assertThat(result.title).isEqualTo(mergedTitle)
        assertThat(result.divisions).isEqualTo(mergedDivisions)
        assertThat(result.authors).isEqualTo(mergedAuthors)
        assertThat(result.largeThumbnail).isEqualTo(mergedLargeThumbnail)
        assertThat(result.mediumThumbnail).isEqualTo(mergedMediumThumbnail)
        assertThat(result.description).isEqualTo(mergedDescription)
        assertThat(result.price).isEqualTo(mergedPrice)
        assertThat(result.original).isEqualTo(baseOriginalProperty + itemOriginalProperty)
    }

}