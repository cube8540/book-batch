package cube8540.book.batch.book.application

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController
import cube8540.book.batch.book.repository.BookDetailsRepository

class DefaultBookCommandService(
    private val bookDetailsRepository: BookDetailsRepository,
    private val bookDataController: BookDetailsController
): BookCommandService {
    override fun upsertBookDetails(books: List<BookDetails>) {
        val entities = ArrayList<BookDetails>()
        if (books.isNotEmpty()) {
            val existsBookDetails = bookDetailsRepository.findById(books.map { it.isbn })
            books.forEach { item ->
                existsBookDetails.find { book -> item.isbn == book.isbn }
                    ?.let { entities.add(bookDataController.merge(it, item)) }
                    ?: entities.add(item)
            }
            bookDetailsRepository.saveAll(entities)
        }
    }

    override fun updateForUpstream(books: List<BookDetails>) {
        bookDetailsRepository.saveAll(books)
    }
}