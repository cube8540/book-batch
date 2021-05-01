package cube8540.book.batch.domain.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.QBookDetails
import org.springframework.stereotype.Repository

@Repository
class BookDetailsCustomRepositoryImpl(val queryFactory: JPAQueryFactory): BookDetailsCustomRepository {
    val bookDetails: QBookDetails = QBookDetails.bookDetails

    override fun findById(isbn: List<String>): List<BookDetails> {
        return queryFactory.selectFrom(bookDetails)
            .leftJoin(bookDetails.divisions).fetchJoin()
            .leftJoin(bookDetails.authors).fetchJoin()
            .leftJoin(bookDetails.keywords).fetchJoin()
            .leftJoin(bookDetails.original).fetchJoin()
            .where(bookDetails.isbn.`in`(isbn))
            .fetch()
    }
}