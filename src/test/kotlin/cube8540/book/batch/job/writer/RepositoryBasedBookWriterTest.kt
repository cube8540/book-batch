package cube8540.book.batch.job.writer

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.repository.BookDetailsRepository
import cube8540.book.batch.external.BookDetailsController
import io.mockk.*
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
    fun `upsert to merged returns null`() {
        val item0000: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "isbn0000"
        }
        val item0001: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "isbn0001"
        }
        val item0002: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "isbn0002"
        }
        val item0003: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "isbn0003"
        }

        val existsBook0: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "isbn0001"
        }
        val existsBook1: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "isbn0002"
        }

        val mergedBook0: BookDetails = mockk(relaxed = true)

        val items = listOf(item0000, item0001, item0002, item0003).toMutableList()
        every { repository.findById(listOf("isbn0000", "isbn0001", "isbn0002", "isbn0003")) } returns listOf(existsBook0, existsBook1)
        every { controller.merge(existsBook0, item0001) } returns mergedBook0
        every { controller.merge(existsBook1, item0002) } returns null

        writer.write(items)
        verify { repository.saveAll(listOf(item0000, item0003)) }
        verify { repository.saveAll(listOf(mergedBook0)) }
    }

    @Test
    fun `upsert to repository`() {
        val item0000: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "isbn0000"
        }
        val item0001: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "isbn0001"
        }
        val item0002: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "isbn0002"
        }
        val item0003: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "isbn0003"
        }

        val existsBook0: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "isbn0001"
        }
        val existsBook1: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "isbn0002"
        }

        val mergedBook0: BookDetails = mockk(relaxed = true)
        val mergedBook1: BookDetails = mockk(relaxed = true)

        val items = listOf(item0000, item0001, item0002, item0003).toMutableList()
        every { repository.findById(listOf("isbn0000", "isbn0001", "isbn0002", "isbn0003")) } returns listOf(existsBook0, existsBook1)
        every { controller.merge(existsBook0, item0001) } returns mergedBook0
        every { controller.merge(existsBook1, item0002) } returns mergedBook1

        writer.write(items)
        verify { repository.saveAll(listOf(item0000, item0003)) }
        verify { repository.saveAll(listOf(mergedBook0, mergedBook1)) }
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }
}