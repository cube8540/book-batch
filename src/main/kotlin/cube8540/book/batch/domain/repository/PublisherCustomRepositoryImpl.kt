package cube8540.book.batch.domain.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.Publisher
import cube8540.book.batch.domain.QPublisher
import cube8540.book.batch.domain.QRawProperty
import org.springframework.stereotype.Repository


@Repository
class PublisherCustomRepositoryImpl(val queryFactory: JPAQueryFactory ): PublisherCustomRepository {

    val publisher: QPublisher = QPublisher.publisher

    override fun findByMappingType(mappingType: MappingType): List<Publisher> {
        val raw = QRawProperty("raw")
        val keyword = QRawProperty("keyword")

        return queryFactory.selectFrom(publisher)
            .leftJoin(publisher.raws, raw).fetchJoin()
            .leftJoin(publisher.keywords, keyword).fetchJoin()
            .where(
                raw.mappingType.eq(mappingType),
                keyword.mappingType.eq(mappingType)
            )
            .fetch()
    }
}