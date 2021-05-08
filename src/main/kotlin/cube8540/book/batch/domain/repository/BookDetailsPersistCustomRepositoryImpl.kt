package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.BookDetails
import org.springframework.stereotype.Repository

@Repository
class BookDetailsPersistCustomRepositoryImpl(private val delegator: BookDetailsPersistMapper)
    : BookDetailsPersistCustomRepository {

    companion object {
        const val defaultBatchSize = 500
    }

    var batchSize = defaultBatchSize

    override fun persistBookDetails(bookDetails: Collection<BookDetails>) {
        if (bookDetails.isNotEmpty()) {
            divideToBatchSize(bookDetails).forEach { delegator.persistBookDetails(it) }
        }
    }

    override fun mergeBookDetails(bookDetails: Collection<BookDetails>) {
        if (bookDetails.isNotEmpty()) {
            divideToBatchSize(bookDetails).forEach { delegator.mergeBookDetails(it) }
        }
    }

    override fun persistDivisions(bookDetails: Collection<BookDetails>) {
        if (bookDetails.isNotEmpty()) {
            val properties: MutableList<BookProperty> = ArrayList()
            bookDetails.forEach {book ->
                book.divisions?.forEach { item -> properties.add(BookProperty(book.isbn, item)) }
            }
            if (properties.isNotEmpty()) {
                divideToBatchSize(properties).forEach { delegator.persistDivisions(it) }
            }
        }
    }

    override fun deleteDivisions(bookDetails: Collection<BookDetails>) {
        if (bookDetails.isNotEmpty()) {
            divideToBatchSize(bookDetails).forEach { delegator.deleteDivisions(it) }
        }
    }

    override fun persistAuthors(bookDetails: Collection<BookDetails>) {
        if (bookDetails.isNotEmpty()) {
            val properties: MutableList<BookProperty> = ArrayList()
            bookDetails.forEach {book ->
                book.authors?.forEach { item -> properties.add(BookProperty(book.isbn, item)) }
            }
            if (properties.isNotEmpty()) {
                divideToBatchSize(properties).forEach { delegator.persistAuthors(it) }
            }
        }
    }

    override fun deleteAuthors(bookDetails: Collection<BookDetails>) {
        if (bookDetails.isNotEmpty()) {
            divideToBatchSize(bookDetails).forEach { delegator.deleteAuthors(it) }
        }
    }

    override fun persistKeywords(bookDetails: Collection<BookDetails>) {
        if (bookDetails.isNotEmpty()) {
            val properties: MutableList<BookProperty> = ArrayList()
            bookDetails.forEach {book ->
                book.keywords?.forEach { item -> properties.add(BookProperty(book.isbn, item)) }
            }
            if (properties.isNotEmpty()) {
                divideToBatchSize(properties).forEach { delegator.persistKeywords(it) }
            }
        }
    }

    override fun deleteKeywords(bookDetails: Collection<BookDetails>) {
        if (bookDetails.isNotEmpty()) {
            divideToBatchSize(bookDetails).forEach { delegator.deleteKeywords(it) }
        }
    }

    override fun persistOriginals(bookDetails: Collection<BookDetails>) {
        if (bookDetails.isNotEmpty()) {
            val properties: MutableList<BookOriginalProperty> = ArrayList()
            bookDetails.forEach {book ->
                book.original?.forEach { item ->
                    properties.add(BookOriginalProperty(book.isbn, item.key.property, item.key.mappingType, item.value))
                }
            }
            if (properties.isNotEmpty()) {
                divideToBatchSize(properties).forEach { delegator.persistOriginals(it) }
            }
        }
    }

    override fun deleteOriginals(bookDetails: Collection<BookDetails>) {
        if (bookDetails.isNotEmpty()) {
            divideToBatchSize(bookDetails).forEach { delegator.deleteOriginals(it) }
        }
    }

    override fun updateForUpstreamTarget(bookDetails: Collection<BookDetails>) {
        if (bookDetails.isNotEmpty()) {
            divideToBatchSize(bookDetails).forEach { delegator.updateForUpstreamTarget(it) }
        }
    }

    private fun <E> divideToBatchSize(targets: Collection<E>): List<List<E>> = targets.chunked(batchSize)
}
