package cube8540.book.batch.job.writer

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController
import cube8540.book.batch.book.repository.BookDetailsRepository
import org.springframework.batch.item.support.AbstractItemStreamItemWriter

open class RepositoryBasedBookWriter(
    private val bookDetailsRepository: BookDetailsRepository,
    private val controller: BookDetailsController
): AbstractItemStreamItemWriter<BookDetails>() {

    override fun write(items: MutableList<out BookDetails>) {
        val entities = ArrayList<BookDetails>()
        if (items.isNotEmpty()) {
            val existsBookDetails = bookDetailsRepository.findById(items.map { it.isbn })
            items.forEach { item ->
                existsBookDetails.find { book -> item.isbn == book.isbn }
                    ?.let { entities.add(controller.merge(it, item)) }
                    ?: entities.add(item)
            }
            bookDetailsRepository.saveAll(entities)
        }
    }
}