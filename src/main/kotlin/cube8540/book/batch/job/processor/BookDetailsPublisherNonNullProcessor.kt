package cube8540.book.batch.job.processor

import cube8540.book.batch.book.domain.BookDetails
import org.springframework.batch.item.ItemProcessor

open class BookDetailsPublisherNonNullProcessor: ItemProcessor<BookDetails, BookDetails> {
    override fun process(item: BookDetails): BookDetails? = when (item.publisher != null) {
        true -> item
        else -> null
    }
}