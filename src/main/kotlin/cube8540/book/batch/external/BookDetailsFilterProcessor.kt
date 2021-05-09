package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetails
import io.github.cube8540.validator.core.Validatable
import org.springframework.batch.item.ItemProcessor

class BookDetailsFilterProcessor(private val validator: Validatable<BookDetails>): ItemProcessor<BookDetails, BookDetails> {
    override fun process(item: BookDetails): BookDetails? =
        if (validator.isValid(item)) {
            item
        } else {
            null
        }
}