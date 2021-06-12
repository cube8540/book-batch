package cube8540.book.batch.book.domain

import io.github.cube8540.validator.core.Validatable

interface PublisherRawMapper {
    fun mapping(raw: String): String?
}

interface DivisionRawMapper {
    fun mapping(raws: List<String>): List<String>
}

interface BookDetailsFilterFunction: Validatable<BookDetails>