package cube8540.book.batch.job.processor

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsContext
import org.springframework.batch.item.ItemProcessor

open class ContextToBookDetailsProcessor: ItemProcessor<BookDetailsContext, BookDetails> {
    override fun process(item: BookDetailsContext): BookDetails = BookDetails(item)
}