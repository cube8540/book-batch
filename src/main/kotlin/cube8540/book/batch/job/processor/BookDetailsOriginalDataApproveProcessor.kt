package cube8540.book.batch.job.processor

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsFilterFunction
import cube8540.book.batch.book.domain.MappingType
import org.springframework.batch.item.ItemProcessor

class BookDetailsOriginalDataApproveProcessor(): ItemProcessor<BookDetails, BookDetails> {

    private val filterMap = HashMap<MappingType, BookDetailsFilterFunction>()

    constructor(vararg pair: Pair<MappingType, BookDetailsFilterFunction>): this() {
        pair.forEach { add(it.first, it.second) }
    }

    override fun process(item: BookDetails): BookDetails? {
        val mappingTypes = item.original?.keys?.map { it.mappingType }?.distinct() ?: emptyList()

        return if (mappingTypes.all { filterMap[it]?.isValid(item) != false }) {
            item
        } else {
            null
        }
    }

    fun add(mappingType: MappingType, filter: BookDetailsFilterFunction) {
        filterMap[mappingType] = filter
    }

    fun clear() {
        filterMap.clear()
    }
}