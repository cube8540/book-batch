package cube8540.book.batch.job.reader

import cube8540.book.batch.book.domain.BookDetailsContext
import cube8540.book.batch.interlock.ExternalBookAPIExchanger
import cube8540.book.batch.job.BookAPIRequestJobParameter
import org.springframework.batch.item.database.AbstractPagingItemReader

open class WebClientBookReader(private val exchanger: ExternalBookAPIExchanger, private val parameter: BookAPIRequestJobParameter)
    : AbstractPagingItemReader<BookDetailsContext>() {

    override fun doReadPage() {
        val exchange = exchanger.exchange(parameter.toRequest(page + 1, pageSize))
        if (results == null) {
            results = ArrayList()
        }
        if (exchange == null || exchange.books.isEmpty()) {
            results.clear()
        } else {
            results.addAll(exchange.books)
        }
    }

    override fun doJumpToPage(itemIndex: Int) {
    }
}