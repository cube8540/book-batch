package cube8540.book.batch.book.application

import cube8540.book.batch.book.domain.createBookDetails
import cube8540.book.batch.book.repository.BookDetailsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate

class UnDetachedBookDetailsServiceTest {

    private val bookDetailsRepository: BookDetailsRepository = mockk(relaxed = true)

    private val service = UnDetachedBookQueryService(bookDetailsRepository)

    @Test
    fun `un detached load book details`() {
        val from = LocalDate.of(2021, 1, 1)
        val to = LocalDate.of(2021, 6, 6)
        val pageRequest: PageRequest = mockk(relaxed = true)

        val bookDetails = listOf(
            createBookDetails(isbn = "isbn0000"),
            createBookDetails(isbn = "isbn0001"),
            createBookDetails(isbn = "isbn0002")
        )

        every { bookDetailsRepository.findByPublishDateBetween(from, to, pageRequest) } returns PageImpl(bookDetails)

        val results = service.loadBookDetails(from, to, pageRequest)
        Assertions.assertThat(results).isEqualTo(PageImpl(bookDetails))
        verify(exactly = 0) { bookDetailsRepository.detached(bookDetails) }
    }
}