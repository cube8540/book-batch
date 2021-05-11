package cube8540.book.batch.infra

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.repository.BookDetailsRepository
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
        val item0000 = BookDetails("isbn0000")
        val item0001 = BookDetails("isbn0001")
        val item0002 = BookDetails("isbn0002")
        val item0003 = BookDetails("isbn0003")

        val existsBook0 = BookDetails("isbn0001")
        val existsBook1 = BookDetails("isbn0002")

        val mergedBook0: BookDetails = mockk(relaxed = true)

        val items = listOf(item0000, item0001, item0002, item0003).toMutableList()
        every { repository.findById(listOf("isbn0000", "isbn0001", "isbn0002", "isbn0003")) } returns listOf(existsBook0, existsBook1)
        every { controller.merge(existsBook0, item0001) } returns mergedBook0
        every { controller.merge(existsBook1, item0002) } returns null

        writer.write(items)
        verifyPersist(listOf(item0000, item0003))
        verifyMerged(listOf(mergedBook0))
    }

    @Test
    fun `upsert to repository`() {
        val item0000 = BookDetails("isbn0000")
        val item0001 = BookDetails("isbn0001")
        val item0002 = BookDetails("isbn0002")
        val item0003 = BookDetails("isbn0003")

        val existsBook0 = BookDetails("isbn0001")
        val existsBook1 = BookDetails("isbn0002")

        val mergedBook0: BookDetails = mockk(relaxed = true)
        val mergedBook1: BookDetails = mockk(relaxed = true)

        val items = listOf(item0000, item0001, item0002, item0003).toMutableList()
        every { repository.findById(listOf("isbn0000", "isbn0001", "isbn0002", "isbn0003")) } returns listOf(existsBook0, existsBook1)
        every { controller.merge(existsBook0, item0001) } returns mergedBook0
        every { controller.merge(existsBook1, item0002) } returns mergedBook1

        writer.write(items)
        verifyPersist(listOf(item0000, item0003))
        verifyMerged(listOf(mergedBook0, mergedBook1))
    }

    private fun verifyPersist(collection: Collection<BookDetails>) {
        verify { repository.persistBookDetails(collection) }
        verify { repository.persistDivisions(collection) }
        verify { repository.persistAuthors(collection) }
        verify { repository.persistKeywords(collection) }
        verify { repository.persistOriginals(collection) }
    }

    private fun verifyMerged(collection: Collection<BookDetails>) {
        verifyOrder {
            repository.mergeBookDetails(collection)

            repository.deleteDivisions(collection)
            repository.deleteAuthors(collection)
            repository.deleteKeywords(collection)
            repository.deleteOriginals(collection)

            repository.persistDivisions(collection)
            repository.persistAuthors(collection)
            repository.persistKeywords(collection)
            repository.persistOriginals(collection)
        }
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }
}