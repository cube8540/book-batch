package cube8540.book.batch.infra

import cube8540.book.batch.Reloadable
import cube8540.book.batch.domain.Division
import cube8540.book.batch.domain.DivisionRawMapper
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.RawProperty
import cube8540.book.batch.domain.repository.DivisionCustomRepository

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