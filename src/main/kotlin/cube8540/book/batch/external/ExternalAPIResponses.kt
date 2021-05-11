package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetailsContext

class BookAPIResponse(val totalCount: Long, val page: Long, val books: List<BookDetailsContext>)

class BookAPIErrorResponse(val code: String, val message: String)