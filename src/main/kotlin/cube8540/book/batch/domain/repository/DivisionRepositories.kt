package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.Division
import cube8540.book.batch.domain.MappingType
import org.springframework.data.jpa.repository.JpaRepository

interface DivisionCustomRepository: CustomRepository<Division> {
    fun findByMappingType(mappingType: MappingType): List<Division>
}

interface DivisionRepository: JpaRepository<Division, String>, DivisionCustomRepository