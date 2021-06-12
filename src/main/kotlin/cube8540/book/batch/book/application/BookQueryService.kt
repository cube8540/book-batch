package cube8540.book.batch.book.application

import cube8540.book.batch.book.domain.BookDetails
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.time.LocalDate

interface BookQueryService {
    fun loadBookDetails(publishFrom: LocalDate, publishTo: LocalDate, pageRequest: PageRequest): Page<BookDetails>
}