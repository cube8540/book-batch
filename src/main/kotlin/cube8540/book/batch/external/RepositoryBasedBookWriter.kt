package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.repository.BookDetailsRepository
import org.springframework.batch.item.ItemWriter

open class RepositoryBasedBookWriter(
    private val bookDetailsRepository: BookDetailsRepository,
    private val controller: BookDetailsController
): ItemWriter<BookDetails> {

    override fun write(items: MutableList<out BookDetails>) {
        val persistList = ArrayList<BookDetails>()
        val mergeList = ArrayList<BookDetails>()

        val existsBookDetails = bookDetailsRepository.findById(items.map { it.isbn })
        items.forEach { item ->
            val exists = existsBookDetails.find { book -> item.isbn == book.isbn }
            if (exists != null) {
                controller.merge(exists, item)?.let { mergeList.add(it) }
            } else {
                persistList.add(item)
            }
        }

        persist(persistList)
        merge(mergeList)
    }

    private fun persist(items: Collection<BookDetails>) {
        bookDetailsRepository.persistBookDetails(items)
        persistProperties(items)
    }

    private fun merge(items: Collection<BookDetails>) {
        bookDetailsRepository.mergeBookDetails(items)
        removeProperties(items)
        persistProperties(items)
    }

    private fun persistProperties(items: Collection<BookDetails>) {
        bookDetailsRepository.persistDivisions(items)
        bookDetailsRepository.persistAuthors(items)
        bookDetailsRepository.persistKeywords(items)
        bookDetailsRepository.persistOriginals(items)
    }

    private fun removeProperties(items: Collection<BookDetails>) {
        bookDetailsRepository.deleteDivisions(items)
        bookDetailsRepository.deleteAuthors(items)
        bookDetailsRepository.deleteKeywords(items)
        bookDetailsRepository.deleteOriginals(items)
    }
}