package cube8540.book.batch.book.repository

import cube8540.book.batch.book.domain.BookDetails
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface BookDetailsCustomRepository: CustomRepository<BookDetails> {
    fun findById(isbn: List<String>): List<BookDetails>

    fun findByPublishDateBetween(from: LocalDate, to: LocalDate, pageRequest: PageRequest): Page<BookDetails>
}

interface BookDetailsRepository: JpaRepository<BookDetails, String>, BookDetailsCustomRepository