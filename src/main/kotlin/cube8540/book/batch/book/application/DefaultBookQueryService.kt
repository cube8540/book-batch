package cube8540.book.batch.book.application

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.repository.BookDetailsRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DefaultBookQueryService(private val bookDetailsRepository: BookDetailsRepository): BookQueryService {
    override fun loadBookDetails(publishFrom: LocalDate, publishTo: LocalDate, pageRequest: PageRequest): Page<BookDetails> {
        val page = bookDetailsRepository.findByPublishDateBetween(publishFrom, publishTo, pageRequest)

        bookDetailsRepository.detached(page.content)
        return page
    }
}