package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.BookOriginalFilter
import cube8540.book.batch.domain.MappingType
import org.springframework.data.jpa.repository.JpaRepository

interface BookOriginalFilterCustomRepository: CustomRepository<BookOriginalFilter> {
    fun findRootByMappingType(mappingType: MappingType): BookOriginalFilter?
}

interface BookOriginalFilterRepository: JpaRepository<BookOriginalFilter, String>, BookOriginalFilterCustomRepository