package cube8540.book.batch.book.repository

import cube8540.book.batch.book.domain.BookOriginalFilter
import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.domain.QBookOriginalFilter
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class BookOriginalFilterCustomRepositoryImpl: BookOriginalFilterCustomRepository, QuerydslRepositorySupport(
    BookOriginalFilter::class.java) {

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