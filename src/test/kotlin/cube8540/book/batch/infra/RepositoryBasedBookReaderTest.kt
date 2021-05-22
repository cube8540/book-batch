package cube8540.book.batch.infra

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.repository.BookDetailsRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import kotlin.random.Random

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class RepositoryBasedBookReaderTest {

    private val repository: BookDetailsRepository = mockk(relaxed = true)

    private val pageSize = 100
    private val from = LocalDate.of(2021, 5, 1)
    private val to = LocalDate.of(2021, 5, 31)
    private val totalCount = Random.nextInt(10)

    private val reader = RepositoryBasedBookReader(repository, from, to)

    init {
        reader.pageSize = pageSize
    }

    @Test
    fun `read value`() {
        val firstBookDetails: BookDetails = mockk(relaxed = true)
        val requestPage = PageRequest.of(reader.page, pageSize)

        val bookDetails: List<BookDetails> = listOf(firstBookDetails, mockk(relaxed = true), mockk(relaxed = true))
        val pageResult = PageImpl(bookDetails, requestPage, totalCount.toLong())

        every { repository.findByPublishDateBetween(from, to, requestPage) } returns pageResult

        val result = reader.read()
        assertThat(result).isEqualTo(firstBookDetails)
    }

    @Test
    fun `database returns empty data when results already not empty`() {
        val firstRequestPage = PageRequest.of(reader.page, pageSize)
        val secondRequestPage = PageRequest.of(reader.page + 1, pageSize)

        val bookDetails: List<BookDetails> = listOf(mockk(relaxed = true))
        val firstReaderPageResult = PageImpl(bookDetails, firstRequestPage, totalCount.toLong())

        every { repository.findByPublishDateBetween(from, to, firstRequestPage) } returns firstReaderPageResult
        every { repository.findByPublishDateBetween(from, to, secondRequestPage) } returns PageImpl(emptyList(), secondRequestPage, 0)
        reader.read() // 한번 데이터를 읽어서 results 를 채워 놓음

        val result = reader.read()
        assertThat(result).isNull()
    }

}