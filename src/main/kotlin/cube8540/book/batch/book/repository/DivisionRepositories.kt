package cube8540.book.batch.book.repository

import cube8540.book.batch.book.domain.Division
import cube8540.book.batch.book.domain.MappingType
import org.springframework.data.jpa.repository.JpaRepository

interface DivisionCustomRepository: CustomRepository<Division> {
    fun findByMappingType(mappingType: MappingType): List<Division>
}

interface DivisionRepository: JpaRepository<Division, String>, DivisionCustomRepository