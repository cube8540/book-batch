package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.external.BookDocumentMapper
import cube8540.book.batch.external.exception.ExternalException
import org.jsoup.Jsoup
import org.springframework.batch.item.ItemProcessor
import org.springframework.web.reactive.function.client.WebClient

class KyoboWebClientBookProcessor(
    private val webClient: WebClient,
    private val documentMapper: BookDocumentMapper
): ItemProcessor<BookDetails, BookDetails> {

    override fun process(item: BookDetails): BookDetails? {
        val result = exchange(item.isbn)
        return try {
            merge(item, documentMapper.convertValue(Jsoup.parse(result)))
        } catch (e: ExternalException) {
            null
        }
    }

    private fun merge(base: BookDetails, item: BookDetails): BookDetails {
        base.title = item.title
        base.authors = item.authors
        base.largeThumbnail = item.largeThumbnail
        base.mediumThumbnail = item.mediumThumbnail
        base.price = item.price
        base.divisions = item.divisions
        base.seriesCode = item.seriesCode
        base.description = item.description

        return base
    }

    private fun exchange(isbn: String): String? = webClient.get()
        .uri { uri ->
            uri.path(KyoboBookRequestNames.kyoboBookDetailsPath)
                .queryParam(KyoboBookRequestNames.isbn, isbn)
                .build()
        }
        .retrieve()
        .bodyToMono(String::class.java)
        .block()
}