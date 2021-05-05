package cube8540.book.batch.domain.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import cube8540.book.batch.domain.Division
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.QDivision
import cube8540.book.batch.domain.QRawProperty
import org.springframework.stereotype.Repository

@Repository
class DivisionCustomRepositoryImpl(val queryFactory: JPAQueryFactory): DivisionCustomRepository {

    val division: QDivision = QDivision.division

    override fun findByMappingType(mappingType: MappingType): List<Division> {
        val raw = QRawProperty("raw")

        return queryFactory.selectFrom(division)
            .leftJoin(division.raws, raw).fetchJoin()
            .where(raw.mappingType.eq(mappingType))
            .fetch()
    }
}