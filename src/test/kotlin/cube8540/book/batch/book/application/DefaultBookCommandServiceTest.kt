package cube8540.book.batch.book.application

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController
import cube8540.book.batch.book.domain.createBookDetails
import cube8540.book.batch.book.repository.BookDetailsRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DefaultBookCommandServiceTest {

    private val bookDetailsRepository: BookDetailsRepository = mockk(relaxed = true)
    private val bookDetailsController: BookDetailsController = mockk(relaxed = true)

    private val service = DefaultBookCommandService(bookDetailsRepository, bookDetailsController)

    @Nested
    inner class UpsertTest {

        @Test
        fun `item is empty`() {
            service.upsertBookDetails(emptyList())

            verify { bookDetailsRepository wasNot Called }
        }

        @Test
        fun `insert request book`() {
            val requestList = mutableListOf(
                createBookDetails(isbn = "originalIsbn0000"),
                createBookDetails(isbn = "originalIsbn0001"),
                createBookDetails(isbn = "originalIsbn0002")
            )
            val insertedBookCaptor = slot<Iterable<BookDetails>>()

            every { bookDetailsRepository.findById(requestList.map { it.isbn }) } returns emptyList()
            every { bookDetailsRepository.saveAll(capture(insertedBookCaptor)) } returnsArgument 0

            service.upsertBookDetails(requestList)
            assertThat(insertedBookCaptor.captured).isEqualTo(requestList)
        }

        @Test
        fun `update request book`() {
            val requestList = mutableListOf(
                createBookDetails(isbn = "requestIsbn00000"),
                createBookDetails(isbn = "requestIsbn00001"),
                createBookDetails(isbn = "requestIsbn00002")
            )
            val existsList = listOf(
                createBookDetails(isbn = "requestIsbn00000", isNew = false),
                createBookDetails(isbn = "requestIsbn00001", isNew = false),
                createBookDetails(isbn = "requestIsbn00002", isNew = false)
            )
            val mergedResultBookList = listOf(
                createBookDetails(isbn = "mergedIsbn0000", isNew = false),
                createBookDetails(isbn = "mergedIsbn0001", isNew = false),
                createBookDetails(isbn = "mergedIsbn0002", isNew = false)
            )
            val existsBookCaptor = mutableListOf<BookDetails>()
            val updatedBookCaptor = slot<Iterable<BookDetails>>()

            every { bookDetailsRepository.findById(requestList.map { it.isbn }) } returns existsList
            every { bookDetailsController.merge(capture(existsBookCaptor), requestList[0]) } returns mergedResultBookList[0]
            every { bookDetailsController.merge(capture(existsBookCaptor), requestList[1]) } returns mergedResultBookList[1]
            every { bookDetailsController.merge(capture(existsBookCaptor), requestList[2]) } returns mergedResultBookList[2]
            every { bookDetailsRepository.saveAll(capture(updatedBookCaptor)) } returnsArgument 0

            service.upsertBookDetails(requestList)
            assertThat(existsBookCaptor).usingFieldByFieldElementComparator().containsAll(existsList)
            assertThat(updatedBookCaptor.captured).usingFieldByFieldElementComparator().containsAll(mergedResultBookList)
        }

        @Test
        fun `insert and update`() {
            val requestList = mutableListOf(
                createBookDetails(isbn = "requestIsbn00000"),
                createBookDetails(isbn = "requestIsbn00001"),
                createBookDetails(isbn = "requestIsbn00002")
            )
            val existsList = listOf(createBookDetails(isbn = "requestIsbn00001", isNew = false))
            val mergedResultBook = createBookDetails(isbn = "mergedIsbn0001", isNew = false)
            val existsBookCaptor = slot<BookDetails>()
            val upsertBookCaptor = slot<Iterable<BookDetails>>()

            every { bookDetailsRepository.findById(requestList.map { it.isbn }) } returns existsList
            every { bookDetailsController.merge(capture(existsBookCaptor), requestList[1]) } returns mergedResultBook
            every { bookDetailsRepository.saveAll(capture(upsertBookCaptor)) } returnsArgument 0

            service.upsertBookDetails(requestList)
            assertThat(existsBookCaptor.captured).isEqualToComparingFieldByField(existsList[0])
            assertThat(upsertBookCaptor.captured).hasSize(3)
                .usingFieldByFieldElementComparator()
                .isEqualTo(listOf(requestList[0], mergedResultBook, requestList[2]))

        }

        @AfterEach
        fun cleanup() {
            clearAllMocks()
        }
    }
}