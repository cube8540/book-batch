package cube8540.book.batch.infra

import cube8540.book.batch.Reloadable
import cube8540.book.batch.domain.*
import cube8540.book.batch.domain.repository.DivisionCustomRepository
import cube8540.book.batch.domain.repository.PublisherCustomRepository

class DefaultPublisherRawMapper(private val mappingType: MappingType, private val repository: PublisherCustomRepository): PublisherRawMapper,
    Reloadable {

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

class DefaultDivisionRawMapper(private val mappingType: MappingType, private val repository: DivisionCustomRepository): DivisionRawMapper, Reloadable {

    val catch: MutableList<Division> = ArrayList()

    init {
        this.catch.addAll(repository.findByMappingType(mappingType))
    }

    override fun mapping(raws: List<String>): List<String> {
        return catch
            .filter { division -> raws.any { raw -> division.raws.contains(RawProperty(raw, mappingType)) } }
            .map { it.code }
    }

    override fun reload() {
        catch.clear()
        catch.addAll(repository.findByMappingType(mappingType))
    }
}