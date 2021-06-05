package cube8540.book.batch.book.infra

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CompositeBookDetailsControllerTest {

    @Test
    fun `merged when delegators is empty`() {
        val base: BookDetails = mockk(relaxed = true)
        val item: BookDetails = mockk(relaxed = true)

        val controller = CompositeBookDetailsController()

        val result = controller.merge(base, item)
        assertThat(result).isEqualTo(base)
    }

    @Test
    fun `returns null during merge`() {
        val base: BookDetails = mockk(relaxed = true)
        val item: BookDetails = mockk(relaxed = true)

        val firstMergedResult: BookDetails = mockk(relaxed = true)
        val firstMergedController: BookDetailsController = mockk {
            every { merge(base, item) } returns firstMergedResult
        }
        val secondMergedResult = null
        val secondMergedController: BookDetailsController = mockk {
            every { merge(firstMergedResult, item) } returns secondMergedResult
        }
        val thirdMergedResult: BookDetails = mockk(relaxed = true)
        val thirdMergedController: BookDetailsController = mockk {
            every { merge(any(), item) } returns thirdMergedResult
        }

        val controller = CompositeBookDetailsController(firstMergedController, secondMergedController, thirdMergedController)
        val result = controller.merge(base, item)
        assertThat(result).isNull()
    }

    @Test
    fun `merge book details`() {
        val base: BookDetails = mockk(relaxed = true)
        val item: BookDetails = mockk(relaxed = true)

        val firstMergedResult: BookDetails = mockk(relaxed = true)
        val firstMergedController: BookDetailsController = mockk {
            every { merge(base, item) } returns firstMergedResult
        }
        val secondMergedResult: BookDetails = mockk(relaxed = true)
        val secondMergedController: BookDetailsController = mockk {
            every { merge(firstMergedResult, item) } returns secondMergedResult
        }
        val thirdMergedResult: BookDetails = mockk(relaxed = true)
        val thirdMergedController: BookDetailsController = mockk {
            every { merge(secondMergedResult, item) } returns thirdMergedResult
        }

        val controller = CompositeBookDetailsController(firstMergedController, secondMergedController, thirdMergedController)
        val result = controller.merge(base, item)
        assertThat(result).isEqualTo(thirdMergedResult)
    }
}