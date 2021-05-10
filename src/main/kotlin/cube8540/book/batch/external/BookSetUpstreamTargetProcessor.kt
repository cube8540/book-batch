package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetails
import org.springframework.batch.item.ItemProcessor

open class BookSetUpstreamTargetProcessor: ItemProcessor<BookDetails, BookDetails> {
    override fun process(item: BookDetails): BookDetails {
        item.isUpstreamTarget = true
        return item
    }
}