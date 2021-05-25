package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.BookOriginalFilter
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.QBookOriginalFilter
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class BookOriginalFilterCustomRepositoryImpl: BookOriginalFilterCustomRepository, QuerydslRepositorySupport(BookOriginalFilter::class.java) {

    val bookOriginalFilter: QBookOriginalFilter = QBookOriginalFilter.bookOriginalFilter

    override fun findRootByMappingType(mappingType: MappingType): BookOriginalFilter? {
        val filters = from(bookOriginalFilter)
            .distinct()
            .leftJoin(bookOriginalFilter.children).fetchJoin()
            .where(bookOriginalFilter.mappingType.eq(mappingType))
            .fetch()
        return filters.find { it.root == true }
    }

    override fun detached(entity: BookOriginalFilter) {
        entityManager!!.detach(entity)
    }

    override fun detached(entities: Collection<BookOriginalFilter>) {
        entities.forEach { entityManager!!.detach(it) }
    }
}