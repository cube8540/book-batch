package cube8540.book.batch.job.writer

import cube8540.book.batch.book.application.BookCommandService
import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookExternalLink
import cube8540.book.batch.translator.BookUpstreamAPIRequest
import cube8540.book.batch.translator.BookUpstreamAPIRequestDetails
import cube8540.book.batch.translator.BookUpstreamExternalLink
import cube8540.book.batch.translator.ExternalBookAPIUpstream
import org.springframework.batch.item.support.AbstractItemStreamItemWriter

open class WebClientBookUpstreamWriter(
    private val externalUpstream: ExternalBookAPIUpstream,

    private val bookCommandService: BookCommandService,
): AbstractItemStreamItemWriter<BookDetails>() {
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
            indexes = it.indexes,
            externalLinks = it.externalLinks?.mapValues { pair -> convertToUpstreamExternalLink(pair.value) },
            confirmedPublication = it.confirmedPublication
        ) }
        externalUpstream.upstream(BookUpstreamAPIRequest(bookUpstreamRequestDetails))

        items.forEach { it.isUpstreamTarget = false }
        bookCommandService.updateForUpstream(items)
    }

    private fun convertToUpstreamExternalLink(externalLink: BookExternalLink): BookUpstreamExternalLink =
        BookUpstreamExternalLink(externalLink.productDetailPage.toString(), externalLink.originalPrice, externalLink.salePrice)
}