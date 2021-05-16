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

    override fun findByMappingTypeWithRaw(mappingType: MappingType): List<Publisher> {
        val raw = QRawProperty("raw")

        return queryFactory.selectFrom(publisher)
            .leftJoin(publisher.raws, raw).fetchJoin()
            .where(raw.mappingType.eq(mappingType))
            .fetch()
    }

    override fun findByMappingTypeWithKeyword(mappingType: MappingType): List<Publisher> {
        val keyword = QRawProperty("keyword")

        return queryFactory.selectFrom(publisher)
            .leftJoin(publisher.keywords, keyword).fetchJoin()
            .where(keyword.mappingType.eq(mappingType))
            .fetch()
    }
}