package cube8540.book.batch.interlock.naver.com

import cube8540.book.batch.interlock.naver.com.application.NaverBookPageDecision
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.random.Random

class NaverBookPageDecisionTest {

    private val pageDecision = NaverBookPageDecision()

    @Test
    fun `page number when first page`() {
        val randomSize = Random.nextInt(10, 100)
        val page = 1

        val result = pageDecision.calculation(page, randomSize)
        assertThat(result).isEqualTo(page)
    }

    @Test
    fun `page number when not first page`() {
        val randomSize = Random.nextInt(10, 100)
        val page = Random.nextInt(2, 100)

        val result = pageDecision.calculation(page, randomSize)
        assertThat(result).isEqualTo((randomSize * (page - 1)) + 1)
    }

}