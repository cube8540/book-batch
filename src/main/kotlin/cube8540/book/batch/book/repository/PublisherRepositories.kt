package cube8540.book.batch.book.repository

import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.domain.Publisher
import org.springframework.data.jpa.repository.JpaRepository

interface PublisherCustomRepository: CustomRepository<Publisher> {
    fun findByMappingTypeWithRaw(mappingType: MappingType): List<Publisher>

    fun findByMappingTypeWithKeyword(mappingType: MappingType): List<Publisher>
}

interface PublisherRepository: JpaRepository<Publisher, String>, PublisherCustomRepository