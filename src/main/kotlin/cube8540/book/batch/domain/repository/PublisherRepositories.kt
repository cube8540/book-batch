package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.Publisher
import org.springframework.data.jpa.repository.JpaRepository

interface PublisherCustomRepository {
    fun findByMappingType(mappingType: MappingType): List<Publisher>
}

interface PublisherRepository: JpaRepository<Publisher, String>, PublisherCustomRepository