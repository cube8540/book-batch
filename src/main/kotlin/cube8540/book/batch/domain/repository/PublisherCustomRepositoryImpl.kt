package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.Publisher
import cube8540.book.batch.domain.QPublisher
import cube8540.book.batch.domain.QRawProperty
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository


@Repository
class PublisherCustomRepositoryImpl: PublisherCustomRepository, QuerydslRepositorySupport(Publisher::class.java) {

    val publisher: QPublisher = QPublisher.publisher

    override fun findByMappingTypeWithRaw(mappingType: MappingType): List<Publisher> {
        val raw = QRawProperty("raw")

        return from(publisher)
            .leftJoin(publisher.raws, raw).fetchJoin()
            .where(raw.mappingType.eq(mappingType))
            .fetch()
    }

    override fun findByMappingTypeWithKeyword(mappingType: MappingType): List<Publisher> {
        val keyword = QRawProperty("keyword")

        return from(publisher)
            .leftJoin(publisher.keywords, keyword).fetchJoin()
            .where(keyword.mappingType.eq(mappingType))
            .fetch()
    }

    override fun detached(entity: Publisher) {
        entityManager!!.detach(entity)
    }

    override fun detached(entities: Collection<Publisher>) {
        entities.forEach { entityManager!!.detach(it) }
    }
}