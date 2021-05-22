package cube8540.book.batch.infra

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.repository.BookDetailsRepository
import org.springframework.batch.item.database.AbstractPagingItemReader
import org.springframework.data.domain.PageRequest
import java.time.LocalDate

open class RepositoryBasedBookReader(
    private val bookDetailsRepository: BookDetailsRepository,

    private val from: LocalDate,
    private val to: LocalDate
): AbstractPagingItemReader<BookDetails>() {

    override fun doReadPage() {
        if (results == null) {
            results = ArrayList()
        } else {
            results.clear()
        }

        val bookDetailsPage = bookDetailsRepository.findByPublishDateBetween(from, to, PageRequest.of(page, pageSize))
        results.addAll(bookDetailsPage.content)
    }

    override fun doJumpToPage(itemIndex: Int) {
    }
}