package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.OriginalPropertyKey
import cube8540.book.batch.domain.Thumbnail
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.author0
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.author1
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.author2
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.createdAt
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.description
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.division0
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.division1
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.division2
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.isbn
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.keyword0
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.keyword1
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.keyword2
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.largeThumbnail
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.mediumThumbnail
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.price
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.propertyKey0
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.propertyKey1
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.propertyValue0
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.propertyValue1
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.publishDate
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.publisher
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.seriesCode
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.smallThumbnail
import cube8540.book.batch.domain.repository.BookDetailsPersistCustomRepositoryImplTestEnvironment.title
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class BookDetailsPersistCustomRepositoryImplTest constructor(val bookDetailsRepository: BookDetailsRepository) {

    private val originalMap = HashMap<OriginalPropertyKey, String?>()
    private val bookDetails0: BookDetails = mockk(relaxed = true)

    init {
        originalMap[propertyKey0] = propertyValue0
        originalMap[propertyKey1] = propertyValue1
        every { bookDetails0.isbn } returns isbn
        every { bookDetails0.title } returns title
        every { bookDetails0.seriesCode } returns seriesCode
        every { bookDetails0.publisher } returns publisher
        every { bookDetails0.publishDate } returns publishDate
        every { bookDetails0.thumbnail } returns Thumbnail(largeThumbnail, mediumThumbnail, smallThumbnail)
        every { bookDetails0.description } returns description
        every { bookDetails0.price } returns price
        every { bookDetails0.createdAt } returns createdAt
        every { bookDetails0.divisions } returns setOf(division0, division1, division2)
        every { bookDetails0.authors } returns setOf(author0, author1, author2)
        every { bookDetails0.keywords } returns setOf(keyword0, keyword1, keyword2)
        every { bookDetails0.original } returns originalMap
    }

    @Test
    @Transactional
    fun `insert book details`() {
        val collection = listOf(bookDetails0)

        bookDetailsRepository.persistBookDetails(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        assertThat(insertBookDetails).isNotNull
        assertThat(insertBookDetails.isbn).isEqualTo(isbn)
        assertThat(insertBookDetails.title).isEqualTo(title)
        assertThat(insertBookDetails.seriesCode).isEqualTo(seriesCode)
        assertThat(insertBookDetails.publisher).isEqualTo(publisher)
        assertThat(insertBookDetails.publishDate).isEqualTo(publishDate)
        assertThat(insertBookDetails.thumbnail?.largeThumbnail).isEqualTo(largeThumbnail)
        assertThat(insertBookDetails.thumbnail?.mediumThumbnail).isEqualTo(mediumThumbnail)
        assertThat(insertBookDetails.thumbnail?.smallThumbnail).isEqualTo(smallThumbnail)
        assertThat(insertBookDetails.description).isEqualTo(description)
        assertThat(insertBookDetails.price).isEqualTo(price)
        assertThat(insertBookDetails.createdAt).isEqualTo(createdAt)
    }

    @Test
    @Transactional
    fun `merge book details`() {
        val bookDetails0 = createTestBook("book0000")
        val bookDetails1 = createTestBook("book0001")
        val bookDetails2 = createTestBook("book0002")

        bookDetailsRepository.persistBookDetails(listOf(bookDetails0, bookDetails1, bookDetails2))
        every { bookDetails0.title } returns "title0000"
        every { bookDetails1.title } returns "title0001"
        every { bookDetails2.title } returns "title0002"
        bookDetailsRepository.mergeBookDetails(listOf(bookDetails0, bookDetails1, bookDetails2))

        val completed0 = bookDetailsRepository.findById("book0000").orElse(null)
        val completed1 = bookDetailsRepository.findById("book0001").orElse(null)
        val completed2 = bookDetailsRepository.findById("book0002").orElse(null)
        assertThat(completed0).isNotNull
        assertThat(completed1).isNotNull
        assertThat(completed2).isNotNull
        assertThat(completed0.title).isEqualTo("title0000")
        assertThat(completed1.title).isEqualTo("title0001")
        assertThat(completed2.title).isEqualTo("title0002")
    }

    @Test
    @Transactional
    fun `insert book detail divisions`() {
        val collection = listOf(bookDetails0)

        bookDetailsRepository.persistBookDetails(collection)
        bookDetailsRepository.persistDivisions(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        assertThat(insertBookDetails.divisions).isEqualTo(setOf(division0, division1, division2))
    }

    @Test
    @Transactional
    fun `delete book detail divisions`() {
        val collection = listOf(bookDetails0)

        bookDetailsRepository.persistBookDetails(collection)
        bookDetailsRepository.persistDivisions(collection)
        bookDetailsRepository.deleteDivisions(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        assertThat(insertBookDetails.divisions).isEmpty()
    }

    @Test
    @Transactional
    fun `insert book detail authors`() {
        val collection = listOf(bookDetails0)

        bookDetailsRepository.persistBookDetails(collection)
        bookDetailsRepository.persistAuthors(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        assertThat(insertBookDetails.authors).isEqualTo(setOf(author0, author1, author2))
    }

    @Test
    @Transactional
    fun `delete book detail authors`() {
        val collection = listOf(bookDetails0)

        bookDetailsRepository.persistBookDetails(collection)
        bookDetailsRepository.persistDivisions(collection)
        bookDetailsRepository.deleteAuthors(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        assertThat(insertBookDetails.authors).isEmpty()
    }

    @Test
    @Transactional
    fun `insert book detail keywords`() {
        val collection = listOf(bookDetails0)

        bookDetailsRepository.persistBookDetails(collection)
        bookDetailsRepository.persistKeywords(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        assertThat(insertBookDetails.keywords).isEqualTo(setOf(keyword0, keyword1, keyword2))
    }

    @Test
    @Transactional
    fun `delete book detail keywords`() {
        val collection = listOf(bookDetails0)

        bookDetailsRepository.persistBookDetails(collection)
        bookDetailsRepository.persistDivisions(collection)
        bookDetailsRepository.deleteKeywords(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        assertThat(insertBookDetails.keywords).isEmpty()
    }

    @Test
    @Transactional
    fun `insert book detail originals`() {
        val collection = listOf(bookDetails0)

        bookDetailsRepository.persistBookDetails(collection)
        bookDetailsRepository.persistOriginals(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        val expectedOriginal = HashMap<OriginalPropertyKey, String?>()
        expectedOriginal[propertyKey0] = propertyValue0
        expectedOriginal[propertyKey1] = propertyValue1

        assertThat(insertBookDetails.original).isEqualTo(expectedOriginal)
    }

    @Test
    @Transactional
    fun `delete book detail originals`() {
        val collection = listOf(bookDetails0)

        bookDetailsRepository.persistBookDetails(collection)
        bookDetailsRepository.persistDivisions(collection)
        bookDetailsRepository.deleteOriginals(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        assertThat(insertBookDetails.original).isEmpty()
    }

    @Test
    @Transactional
    fun `update for book upstream target`() {
        val bookDetails0 = createTestBook("book0000")
        val bookDetails1 = createTestBook("book0001")
        val bookDetails2 = createTestBook("book0002")

        bookDetailsRepository.persistBookDetails(listOf(bookDetails0, bookDetails1, bookDetails2))

        every { bookDetails0.isUpstreamTarget } returns true
        every { bookDetails1.isUpstreamTarget } returns true
        every { bookDetails2.isUpstreamTarget } returns true
        bookDetailsRepository.updateForUpstreamTarget(listOf(bookDetails0, bookDetails1, bookDetails2))

        val completed0 = bookDetailsRepository.findById("book0000").orElse(null)
        val completed1 = bookDetailsRepository.findById("book0001").orElse(null)
        val completed2 = bookDetailsRepository.findById("book0002").orElse(null)
        assertThat(completed0).isNotNull
        assertThat(completed1).isNotNull
        assertThat(completed2).isNotNull
        assertThat(completed0.isUpstreamTarget).isTrue
        assertThat(completed1.isUpstreamTarget).isTrue
        assertThat(completed2.isUpstreamTarget).isTrue
    }

    fun createTestBook(isbn: String): BookDetails {
        val bookDetails: BookDetails = mockk(relaxed = true)
        every { bookDetails.isbn } returns isbn
        every { bookDetails.title } returns title
        every { bookDetails.seriesCode } returns seriesCode
        every { bookDetails.publisher } returns publisher
        every { bookDetails.publishDate } returns publishDate
        every { bookDetails.thumbnail } returns Thumbnail(largeThumbnail, mediumThumbnail, smallThumbnail)
        every { bookDetails.description } returns description
        every { bookDetails.price } returns price
        every { bookDetails.createdAt } returns createdAt
        return bookDetails
    }
}