package cube8540.book.batch.infra

import cube8540.book.batch.Reloadable
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.Publisher
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.domain.RawProperty
import cube8540.book.batch.domain.repository.PublisherCustomRepository

class DefaultPublisherRawMapper(private val mappingType: MappingType, private val repository: PublisherCustomRepository): PublisherRawMapper, Reloadable {

    val cache: MutableList<Publisher> = ArrayList()

    init {
        cache.addAll(repository.findByMappingType(mappingType))
    }

    override fun mapping(raw: String): String? {
        return cache.find { publisher -> publisher.raws.contains(RawProperty(raw, mappingType)) }?.code
    }

    override fun reload() {
        this.cache.clear()
        this.cache.addAll(repository.findByMappingType(mappingType))
    }
}