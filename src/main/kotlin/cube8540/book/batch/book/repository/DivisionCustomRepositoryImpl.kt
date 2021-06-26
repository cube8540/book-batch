package cube8540.book.batch.book.repository

import cube8540.book.batch.book.domain.Division
import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.domain.QDivision
import cube8540.book.batch.book.domain.QRawProperty
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class DivisionCustomRepositoryImpl: DivisionCustomRepository, QuerydslRepositorySupport(Division::class.java) {

    val division: QDivision = QDivision.division

    override fun findByMappingType(mappingType: MappingType): List<Division> {
        val raw = QRawProperty("raw")

        return from(division)
            .leftJoin(division.raws, raw).fetchJoin()
            .where(raw.mappingType.eq(mappingType))
            .fetch()
    }

    override fun detached(entity: Division) {
        entityManager!!.detach(entity)
    }

    override fun detached(entities: Collection<Division>) {
        entities.forEach { entityManager!!.detach(it) }
    }
}