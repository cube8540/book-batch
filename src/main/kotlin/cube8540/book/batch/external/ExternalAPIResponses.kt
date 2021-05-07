package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetails

class BookAPIResponse(val totalCount: Long, val page: Long, val books: List<BookDetails>)

class BookAPIErrorResponse(val code: String, val message: String)