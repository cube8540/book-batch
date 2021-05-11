package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.OriginalPropertyKey
import io.github.cube8540.validator.core.AbstractValidationSpecification
import io.github.cube8540.validator.core.Validatable
import org.springframework.batch.item.ItemProcessor

class BookIsbnNotNullFilter: AbstractValidationSpecification<BookDetails>() {
    override fun isValid(target: BookDetails): Boolean = target.isbn.isNotEmpty()
}

class BookOriginalPropertyRegexFilter(
    private val originalPropertyKey: OriginalPropertyKey,
    private val regex: Regex
): AbstractValidationSpecification<BookDetails>() {
    override fun isValid(target: BookDetails): Boolean = target.original?.get(originalPropertyKey)
        ?.let { regex.matches(it) } ?: false
}