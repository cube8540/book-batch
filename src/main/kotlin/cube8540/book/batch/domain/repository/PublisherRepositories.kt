package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.Publisher
import org.springframework.data.jpa.repository.JpaRepository

interface PublisherCustomRepository {
    fun findByMappingTypeWithRaw(mappingType: MappingType): List<Publisher>

    fun findByMappingTypeWithKeyword(mappingType: MappingType): List<Publisher>
}

interface PublisherRepository: JpaRepository<Publisher, String>, PublisherCustomRepository