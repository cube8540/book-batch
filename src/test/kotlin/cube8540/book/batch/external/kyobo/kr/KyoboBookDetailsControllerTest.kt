package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsControllerTestEnvironment.isbn
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
    fun `merged book details`() {
        val base = BookDetails(isbn)
        val item = BookDetails(isbn)

        item.divisions = mergedDivisions
        item.authors = mergedAuthors
        item.title = mergedTitle
        item.largeThumbnail = mergedLargeThumbnail
        item.mediumThumbnail = mergedMediumThumbnail
        item.description = mergedDescription
        item.price = mergedPrice

        val result = controller.merge(base, item)
        assertThat(result.title).isEqualTo(mergedTitle)
        assertThat(result.divisions).isEqualTo(mergedDivisions)
        assertThat(result.authors).isEqualTo(mergedAuthors)
        assertThat(result.largeThumbnail).isEqualTo(mergedLargeThumbnail)
        assertThat(result.mediumThumbnail).isEqualTo(mergedMediumThumbnail)
        assertThat(result.description).isEqualTo(mergedDescription)
        assertThat(result.price).isEqualTo(mergedPrice)
    }

}