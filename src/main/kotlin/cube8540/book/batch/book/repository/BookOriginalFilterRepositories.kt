package cube8540.book.batch.book.repository

import cube8540.book.batch.book.domain.BookOriginalFilter
import cube8540.book.batch.book.domain.MappingType
import org.springframework.data.jpa.repository.JpaRepository

interface BookOriginalFilterCustomRepository: CustomRepository<BookOriginalFilter> {
    fun findRootByMappingType(mappingType: MappingType): BookOriginalFilter?
}

interface BookOriginalFilterRepository: JpaRepository<BookOriginalFilter, String>, BookOriginalFilterCustomRepository