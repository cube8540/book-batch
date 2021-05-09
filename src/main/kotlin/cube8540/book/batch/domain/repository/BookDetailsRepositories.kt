package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.BookDetails
import org.springframework.data.jpa.repository.JpaRepository

interface BookDetailsPersistCustomRepository {

    fun persistBookDetails(bookDetails: Collection<BookDetails>)

    fun mergeBookDetails(bookDetails: Collection<BookDetails>)

    fun persistDivisions(bookDetails: Collection<BookDetails>)

    fun deleteDivisions(bookDetails: Collection<BookDetails>)

    fun persistAuthors(bookDetails: Collection<BookDetails>)

    fun deleteAuthors(bookDetails: Collection<BookDetails>)

    fun persistKeywords(bookDetails: Collection<BookDetails>)

    fun deleteKeywords(bookDetails: Collection<BookDetails>)

    fun persistOriginals(bookDetails: Collection<BookDetails>)

    fun deleteOriginals(bookDetails: Collection<BookDetails>)

    fun updateForUpstreamTarget(bookDetails: Collection<BookDetails>)
}

interface BookDetailsCustomRepository {
    fun findById(isbn: List<String>): List<BookDetails>
}

interface BookDetailsRepository: JpaRepository<BookDetails, String>, BookDetailsPersistCustomRepository, BookDetailsCustomRepository