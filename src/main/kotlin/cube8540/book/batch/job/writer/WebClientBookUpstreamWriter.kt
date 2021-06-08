package cube8540.book.batch.job.writer

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.external.BookUpstreamAPIRequest
import cube8540.book.batch.external.BookUpstreamAPIRequestDetails
import cube8540.book.batch.external.ExternalBookAPIUpstream
import org.springframework.batch.item.support.AbstractItemStreamItemWriter

open class WebClientBookUpstreamWriter(private val externalUpstream: ExternalBookAPIUpstream): AbstractItemStreamItemWriter<BookDetails>() {
    override fun write(items: MutableList<out BookDetails>) {
        val bookUpstreamRequestDetails = items.map { BookUpstreamAPIRequestDetails(
            isbn = it.isbn,
            title = it.title!!,
            publishDate = it.publishDate!!,
            publisherCode = it.publisher!!,
            seriesIsbn = it.seriesIsbn,
            seriesCode = it.seriesCode,
            largeThumbnail = it.thumbnail?.largeThumbnail,
            mediumThumbnail = it.thumbnail?.mediumThumbnail,
            smallThumbnail = it.thumbnail?.smallThumbnail,
            authors = it.authors?.toList(),
            description = it.description,
            price = it.price
        ) }
        externalUpstream.upstream(BookUpstreamAPIRequest(bookUpstreamRequestDetails))
    }
}