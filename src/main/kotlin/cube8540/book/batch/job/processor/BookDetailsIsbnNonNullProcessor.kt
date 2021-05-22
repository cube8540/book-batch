package cube8540.book.batch.job.processor

import cube8540.book.batch.domain.BookDetails
import org.springframework.batch.item.ItemProcessor

open class BookDetailsIsbnNonNullProcessor: ItemProcessor<BookDetails, BookDetails> {
    override fun process(item: BookDetails): BookDetails? = when (item.isbn.isEmpty()) {
        false -> item
        else -> null
    }
}