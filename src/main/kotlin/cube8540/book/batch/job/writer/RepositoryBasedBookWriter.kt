package cube8540.book.batch.job.writer

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.repository.BookDetailsRepository
import cube8540.book.batch.external.BookDetailsController
import org.springframework.batch.item.support.AbstractItemStreamItemWriter

open class RepositoryBasedBookWriter(
    private val bookDetailsRepository: BookDetailsRepository,
    private val controller: BookDetailsController
): AbstractItemStreamItemWriter<BookDetails>() {

    override fun write(items: MutableList<out BookDetails>) {
        val persistList = ArrayList<BookDetails>()
        val mergeList = ArrayList<BookDetails>()

        if (items.isNotEmpty()) {
            val existsBookDetails = bookDetailsRepository.findById(items.map { it.isbn })
            items.forEach { item ->
                val exists = existsBookDetails.find { book -> item.isbn == book.isbn }
                if (exists != null) {
                    controller.merge(exists, item)?.let { mergeList.add(it) }
                } else {
                    persistList.add(item)
                }
            }
            bookDetailsRepository.saveAll(persistList)
            bookDetailsRepository.saveAll(mergeList)
        }
    }
}