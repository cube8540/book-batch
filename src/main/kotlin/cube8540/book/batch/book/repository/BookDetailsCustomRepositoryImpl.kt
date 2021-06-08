package cube8540.book.batch.book.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.domain.QBookDetails
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class BookDetailsCustomRepositoryImpl: BookDetailsCustomRepository, QuerydslRepositorySupport(BookDetails::class.java) {
    val bookDetails: QBookDetails = QBookDetails.bookDetails

    override fun findById(isbn: List<String>): List<BookDetails> {
        return from(bookDetails)
            .leftJoin(bookDetails.divisions).fetchJoin()
            .leftJoin(bookDetails.authors).fetchJoin()
            .leftJoin(bookDetails.keywords).fetchJoin()
            .leftJoin(bookDetails.original).fetchJoin()
            .where(bookDetails.isbn.`in`(isbn))
            .fetch()
    }

    override fun findByPublishDateBetween(from: LocalDate, to: LocalDate, pageRequest: PageRequest): Page<BookDetails> {
        val queryFactory = JPAQueryFactory(entityManager)
        val queryExpression = queryFactory.select(bookDetails.isbn)
            .from(bookDetails)
            .where(bookDetails.publishDate.between(from, to))
        querydsl!!.applyPagination(pageRequest, queryExpression)

        val queryResults = queryExpression.fetchResults()

        val bookDetailsExpression = from(bookDetails)
            .distinct()
            .leftJoin(bookDetails.divisions).fetchJoin()
            .leftJoin(bookDetails.authors).fetchJoin()
            .leftJoin(bookDetails.keywords).fetchJoin()
            .leftJoin(bookDetails.original).fetchJoin()
            .where(bookDetails.isbn.`in`(queryResults.results))
        querydsl!!.applySorting(pageRequest.sort, bookDetailsExpression)

        return PageImpl(bookDetailsExpression.fetch(), pageRequest, queryResults.total)
    }

    override fun findUpstreamByPublishDateBetween(from: LocalDate, to: LocalDate, pageRequest: PageRequest): Page<BookDetails> {
        val queryFactory = JPAQueryFactory(entityManager)
        val queryExpression = queryFactory.select(bookDetails.isbn)
            .from(bookDetails)
            .where(
                bookDetails.publishDate.between(from, to),
                bookDetails.isUpstreamTarget.isTrue
            )
        querydsl!!.applyPagination(pageRequest, queryExpression)

        val queryResults = queryExpression.fetchResults()

        val bookDetailsExpression = from(bookDetails)
            .distinct()
            .leftJoin(bookDetails.divisions).fetchJoin()
            .leftJoin(bookDetails.authors).fetchJoin()
            .leftJoin(bookDetails.keywords).fetchJoin()
            .where(bookDetails.isbn.`in`(queryResults.results))
        querydsl!!.applySorting(pageRequest.sort, bookDetailsExpression)

        return PageImpl(bookDetailsExpression.fetch(), pageRequest, queryResults.total)
    }

    override fun detached(entity: BookDetails) {
        entityManager!!.detach(entity)
    }

    override fun detached(entities: Collection<BookDetails>) {
        entities.forEach { entityManager!!.detach(it) }
    }
}