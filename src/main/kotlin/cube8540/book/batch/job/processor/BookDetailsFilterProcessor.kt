package cube8540.book.batch.job.processor

import cube8540.book.batch.book.domain.BookDetails
import io.github.cube8540.validator.core.Validatable
import org.springframework.batch.item.ItemProcessor

open class BookDetailsFilterProcessor(private val validator: Validatable<BookDetails>):
    ItemProcessor<BookDetails, BookDetails> {
    override fun process(item: BookDetails): BookDetails? =
        if (validator.isValid(item)) {
            item
        } else {
            null
        }
}