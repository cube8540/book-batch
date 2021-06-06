package cube8540.book.batch.job.writer

import cube8540.book.batch.book.application.BookCommandService
import cube8540.book.batch.book.domain.BookDetails
import org.springframework.batch.item.support.AbstractItemStreamItemWriter

open class RepositoryBasedUpstreamTargetWriter(private val bookCommandService: BookCommandService): AbstractItemStreamItemWriter<BookDetails>() {
    override fun write(items: MutableList<out BookDetails>) {
        bookCommandService.updateForUpstream(items)
    }
}