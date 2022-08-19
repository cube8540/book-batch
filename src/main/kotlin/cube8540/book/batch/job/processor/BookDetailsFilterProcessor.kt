package cube8540.book.batch.job.processor

import cube8540.book.batch.book.domain.BookDetails
import io.github.cube8540.validator.core.Validatable
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor

open class BookDetailsFilterProcessor(private val validator: Validatable<BookDetails>):
    ItemProcessor<BookDetails, BookDetails> {

    private val logger = LoggerFactory.getLogger(BookDetailsFilterProcessor::class.java)

    override fun process(item: BookDetails): BookDetails? {
        val result = validator.isValid(item)

        logger.info("BookDetailsFilterProcessor Result isbn {} ({}) is {}", item.isbn, item, result)
        return if (result) {
            item
        } else {
            null
        }
    }
}