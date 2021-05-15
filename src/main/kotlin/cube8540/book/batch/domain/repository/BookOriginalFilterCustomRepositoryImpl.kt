package cube8540.book.batch.domain.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import cube8540.book.batch.domain.BookOriginalFilter
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.QBookOriginalFilter
import org.springframework.stereotype.Repository

@Repository
class BookOriginalFilterCustomRepositoryImpl(val queryFactory: JPAQueryFactory) : BookOriginalFilterCustomRepository {

    val bookOriginalFilter: QBookOriginalFilter = QBookOriginalFilter.bookOriginalFilter

    override fun findRootByMappingType(mappingType: MappingType): BookOriginalFilter? {
        val filters = queryFactory.selectFrom(bookOriginalFilter)
            .distinct()
            .leftJoin(bookOriginalFilter.children).fetchJoin()
            .where(bookOriginalFilter.mappingType.eq(mappingType))
            .fetch()
        return filters.find { it.root == true }
    }
}