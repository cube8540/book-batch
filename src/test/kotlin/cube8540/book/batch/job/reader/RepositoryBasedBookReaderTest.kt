package cube8540.book.batch.job.reader

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.bookDetailsAssertIgnoringFields
import cube8540.book.batch.book.domain.createBookDetails
import cube8540.book.batch.book.repository.BookDetailsRepository
import cube8540.book.batch.domain.QBookDetails
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDate
import kotlin.random.Random

class RepositoryBasedBookReaderTest {

    private val repository: BookDetailsRepository = mockk(relaxed = true)

    private val pageSize = 100
    private val from = LocalDate.of(2021, 5, 1)
    private val to = LocalDate.of(2021, 5, 31)
    private val totalCount = Random.nextInt(10)

    private lateinit var reader: RepositoryBasedBookReader

    @BeforeEach
    fun setup() {
        reader = RepositoryBasedBookReader(repository, from, to)
        reader.pageSize = pageSize
    }

    @Test
    fun `detached read value when detached set true`() {
        val sort = Sort.by(Sort.Order.desc(QBookDetails.bookDetails.publishDate.metadata.name))
        val requestPage = PageRequest.of(reader.page, pageSize, sort)

        val bookDetails: List<BookDetails> = emptyList()
        val pageResult = PageImpl(bookDetails, requestPage, totalCount.toLong())

        every { repository.findByPublishDateBetween(from, to, requestPage) } returns pageResult
        reader.detached = true

        reader.read()
        verify { repository.detached(bookDetails) }
    }

    @Test
    fun `detached read value when detached set false`() {
        val sort = Sort.by(Sort.Order.desc(QBookDetails.bookDetails.publishDate.metadata.name))
        val requestPage = PageRequest.of(reader.page, pageSize, sort)

        val bookDetails: List<BookDetails> = emptyList()
        val pageResult = PageImpl(bookDetails, requestPage, totalCount.toLong())

        every { repository.findByPublishDateBetween(from, to, requestPage) } returns pageResult
        reader.detached = false

        reader.read()
        verify(exactly = 0) { repository.detached(bookDetails) }
    }

    @Test
    fun `read value`() {
        val sort = Sort.by(Sort.Order.desc(QBookDetails.bookDetails.publishDate.metadata.name))
        val requestPage = PageRequest.of(reader.page, pageSize, sort)
        val bookDetails: List<BookDetails> = listOf(
            createBookDetails(isbn = "isbn0000"),
            createBookDetails(isbn = "isbn0001"),
            createBookDetails(isbn = "isbn0002")
        )
        val pageResult = PageImpl(bookDetails, requestPage, totalCount.toLong())

        every { repository.findByPublishDateBetween(from, to, requestPage) } returns pageResult

        val result = reader.read()
        assertThat(result)
            .isEqualToIgnoringGivenFields(createBookDetails(isbn = "isbn0000"), *bookDetailsAssertIgnoringFields)
    }

    @Test
    fun `database returns empty data when results already not empty`() {
        val sort = Sort.by(Sort.Order.desc(QBookDetails.bookDetails.publishDate.metadata.name))
        val firstRequestPage = PageRequest.of(reader.page, pageSize, sort)
        val secondRequestPage = PageRequest.of(reader.page + 1, pageSize, sort)

        val bookDetails: List<BookDetails> = listOf(createBookDetails(isbn = "isbn0000"))
        val firstReaderPageResult = PageImpl(bookDetails, firstRequestPage, totalCount.toLong())

        every { repository.findByPublishDateBetween(from, to, firstRequestPage) } returns firstReaderPageResult
        every { repository.findByPublishDateBetween(from, to, secondRequestPage) } returns PageImpl(emptyList(), secondRequestPage, 0)
        reader.read() // 한번 데이터를 읽어서 results 를 채워 놓음

        val result = reader.read()
        assertThat(result).isNull()
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

}