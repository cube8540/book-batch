package cube8540.book.batch.job.writer

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController
import cube8540.book.batch.book.domain.createBookDetails
import cube8540.book.batch.book.repository.BookDetailsRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class RepositoryBasedBookWriterTest {

    private val repository: BookDetailsRepository = mockk(relaxed = true)
    private val controller: BookDetailsController = mockk(relaxed = true)

    private val writer = RepositoryBasedBookWriter(repository, controller)

    @Test
    fun `item is empty`() {
        writer.write(emptyList<BookDetails>().toMutableList())

        verify { repository wasNot Called }
    }

    @Test
    fun `insert request book`() {
        val requestList = mutableListOf(
            createBookDetails(isbn = "originalIsbn0000"),
            createBookDetails(isbn = "originalIsbn0001"),
            createBookDetails(isbn = "originalIsbn0002")
        )
        val insertedBookCaptor = slot<Iterable<BookDetails>>()

        every { repository.findById(requestList.map { it.isbn }) } returns emptyList()
        every { repository.saveAll(capture(insertedBookCaptor)) } returnsArgument 0

        writer.write(requestList)
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

        every { repository.findById(requestList.map { it.isbn }) } returns existsList
        every { controller.merge(capture(existsBookCaptor), requestList[0]) } returns mergedResultBookList[0]
        every { controller.merge(capture(existsBookCaptor), requestList[1]) } returns mergedResultBookList[1]
        every { controller.merge(capture(existsBookCaptor), requestList[2]) } returns mergedResultBookList[2]
        every { repository.saveAll(capture(updatedBookCaptor)) } returnsArgument 0

        writer.write(requestList)
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

        every { repository.findById(requestList.map { it.isbn }) } returns existsList
        every { controller.merge(capture(existsBookCaptor), requestList[1]) } returns mergedResultBook
        every { repository.saveAll(capture(upsertBookCaptor)) } returnsArgument 0

        writer.write(requestList)
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