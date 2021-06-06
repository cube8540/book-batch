package cube8540.book.batch.book.application

import cube8540.book.batch.book.domain.BookDetails

interface BookCommandService {
    fun upsertBookDetails(books: List<BookDetails>)

    fun updateForUpstream(books: List<BookDetails>)
}