package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.OriginalPropertyKey
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

    private val bookDetails = BookDetails(isbn = isbn)

    init {
        bookDetails.title = title
        bookDetails.seriesCode = seriesCode
        bookDetails.publisher = publisher
        bookDetails.publishDate = publishDate
        bookDetails.largeThumbnail = largeThumbnail
        bookDetails.mediumThumbnail = mediumThumbnail
        bookDetails.smallThumbnail = smallThumbnail
        bookDetails.description = description
        bookDetails.price = price
        bookDetails.createdAt = createdAt

        bookDetails.divisions = setOf(division0, division1, division2)
        bookDetails.authors = setOf(author0, author1, author2)
        bookDetails.keywords = setOf(keyword0, keyword1, keyword2)

        val originalMap = HashMap<OriginalPropertyKey, String?>()
        originalMap[propertyKey0] = propertyValue0
        originalMap[propertyKey1] = propertyValue1

        bookDetails.original = originalMap
    }

    @Test
    @Transactional
    fun `insert book details`() {
        val collection = listOf(bookDetails)

        bookDetailsRepository.persistBookDetails(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        assertThat(insertBookDetails).isNotNull
        assertThat(insertBookDetails.isbn).isEqualTo(isbn)
        assertThat(insertBookDetails.title).isEqualTo(title)
        assertThat(insertBookDetails.seriesCode).isEqualTo(seriesCode)
        assertThat(insertBookDetails.publisher).isEqualTo(publisher)
        assertThat(insertBookDetails.publishDate).isEqualTo(publishDate)
        assertThat(insertBookDetails.largeThumbnail).isEqualTo(largeThumbnail)
        assertThat(insertBookDetails.mediumThumbnail).isEqualTo(mediumThumbnail)
        assertThat(insertBookDetails.smallThumbnail).isEqualTo(smallThumbnail)
        assertThat(insertBookDetails.description).isEqualTo(description)
        assertThat(insertBookDetails.price).isEqualTo(price)
        assertThat(insertBookDetails.createdAt).isEqualTo(createdAt)
    }

    @Test
    @Transactional
    fun `insert book detail divisions`() {
        val collection = listOf(bookDetails)

        bookDetailsRepository.persistBookDetails(collection)
        bookDetailsRepository.persistDivision(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        assertThat(insertBookDetails.divisions).isEqualTo(setOf(division0, division1, division2))
    }

    @Test
    @Transactional
    fun `insert book detail authors`() {
        val collection = listOf(bookDetails)

        bookDetailsRepository.persistBookDetails(collection)
        bookDetailsRepository.persistAuthors(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        assertThat(insertBookDetails.authors).isEqualTo(setOf(author0, author1, author2))
    }

    @Test
    @Transactional
    fun `insert book detail keywords`() {
        val collection = listOf(bookDetails)

        bookDetailsRepository.persistBookDetails(collection)
        bookDetailsRepository.persistKeywords(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        assertThat(insertBookDetails.keywords).isEqualTo(setOf(keyword0, keyword1, keyword2))
    }

    @Test
    @Transactional
    fun `insert book detail originals`() {
        val collection = listOf(bookDetails)

        bookDetailsRepository.persistBookDetails(collection)
        bookDetailsRepository.persistOriginals(collection)

        val insertBookDetails = bookDetailsRepository.findById(isbn).orElse(null)
        val expectedOriginal = HashMap<OriginalPropertyKey, String?>()
        expectedOriginal[propertyKey0] = propertyValue0
        expectedOriginal[propertyKey1] = propertyValue1

        assertThat(insertBookDetails.original).isEqualTo(expectedOriginal)
    }
}