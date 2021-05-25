package cube8540.book.batch.infra

import cube8540.book.batch.domain.*
import cube8540.book.batch.domain.repository.BookOriginalFilterRepository
import cube8540.book.batch.domain.repository.DivisionCustomRepository
import cube8540.book.batch.domain.repository.PublisherCustomRepository
import org.springframework.beans.factory.InitializingBean

class DefaultPublisherRawMapper(private val mappingType: MappingType, private val repository: PublisherCustomRepository)
    : PublisherRawMapper, InitializingBean, Reloadable {

    val cache: MutableList<Publisher> = ArrayList()

    override fun afterPropertiesSet() {
        cache.addAll(repository.findByMappingTypeWithRaw(mappingType))
        repository.detached(cache)
    }

    override fun mapping(raw: String): String? {
        return cache.find { publisher -> publisher.raws.contains(RawProperty(raw, mappingType)) }?.code
    }

    override fun reload() {
        this.cache.clear()
        this.cache.addAll(repository.findByMappingTypeWithRaw(mappingType))

        repository.detached(cache)
    }
}

class DefaultDivisionRawMapper(private val mappingType: MappingType, private val repository: DivisionCustomRepository)
    : DivisionRawMapper, InitializingBean, Reloadable {

    val cache: MutableList<Division> = ArrayList()

    override fun afterPropertiesSet() {
        this.cache.addAll(repository.findByMappingType(mappingType))
        repository.detached(this.cache)
    }

    override fun mapping(raws: List<String>): List<String> {
        return cache
            .filter { division -> raws.any { raw -> division.raws.contains(RawProperty(raw, mappingType)) } }
            .map { it.code }
    }

    override fun reload() {
        cache.clear()
        cache.addAll(repository.findByMappingType(mappingType))

        repository.detached(this.cache)
    }
}

class DefaultBookDetailsFilterFunction(private val mappingType: MappingType, private val repository: BookOriginalFilterRepository)
    : BookDetailsFilterFunction, InitializingBean, Reloadable {

    var cache: BookOriginalFilter? = null
        private set

    override fun afterPropertiesSet() {
        this.cache = repository.findRootByMappingType(mappingType)
        cache?.let { repository.detached(it) }
    }

    override fun isValid(target: BookDetails?): Boolean = target?.let { t -> cache?.isValid(t) ?: true } ?: true

    override fun reload() {
        this.cache = repository.findRootByMappingType(mappingType)
        cache?.let { repository.detached(it) }
    }
}