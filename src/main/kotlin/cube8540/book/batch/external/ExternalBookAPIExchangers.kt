package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetailsContext
import java.time.LocalDate

interface ExternalBookAPIExchanger {
    fun exchange(request: BookAPIRequest): BookAPIResponse?
}

data class BookAPIRequest(
    val page: Int? = null,
    val size: Int? = null,
    val from: LocalDate? = null,
    val to: LocalDate? = null,
    val isbn: String? = null,
    val publisher: String? = null
)

data class BookAPIResponse(
    val totalCount: Long,
    val page: Long,
    val books: List<BookDetailsContext>
)

data class BookAPIErrorResponse(
    val code: String,
    val message: String
)