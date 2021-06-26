package cube8540.book.batch.job.reader

import cube8540.book.batch.book.application.BookQueryService
import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.QBookDetails
import org.springframework.batch.item.database.AbstractPagingItemReader
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDate

open class RepositoryBasedBookReader(
    private val bookDetailsService: BookQueryService,

    private val from: LocalDate,
    private val to: LocalDate
): AbstractPagingItemReader<BookDetails>() {

    override fun doReadPage() {
        if (results == null) {
            results = ArrayList()
        } else {
            results.clear()
        }

        val sort = Sort.by(Sort.Order.desc(QBookDetails.bookDetails.publishDate.metadata.name))
        val bookDetailsPage = bookDetailsService.loadBookDetails(from, to, PageRequest.of(page, pageSize, sort))

        results.addAll(bookDetailsPage.content)
    }

    override fun doJumpToPage(itemIndex: Int) {
    }
}