package cube8540.book.batch.job.writer

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.repository.BookDetailsRepository
import org.springframework.batch.item.support.AbstractItemStreamItemWriter

open class RepositoryBasedUpstreamTargetWriter(
    private val bookDetailsRepository: BookDetailsRepository
): AbstractItemStreamItemWriter<BookDetails>() {
    override fun write(items: MutableList<out BookDetails>) {
        bookDetailsRepository.saveAll(items)
    }
}