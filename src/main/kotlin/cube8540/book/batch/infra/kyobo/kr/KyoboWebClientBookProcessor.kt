package cube8540.book.batch.infra.kyobo.kr

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.external.BookDetailsController
import cube8540.book.batch.external.BookDocumentMapper
import cube8540.book.batch.external.exception.ExternalException
import org.jsoup.Jsoup
import org.springframework.batch.item.ItemProcessor
import org.springframework.web.reactive.function.client.WebClient

class KyoboWebClientBookProcessor(
    private val webClient: WebClient,
    private val documentMapper: BookDocumentMapper
): ItemProcessor<BookDetails, BookDetails> {

    var controller: BookDetailsController = KyoboBookDetailsController()

    override fun process(item: BookDetails): BookDetails? {
        return try {
            val result = exchange(item.isbn)
            val documentContext = documentMapper.convertValue(Jsoup.parse(result))
            controller.merge(item, BookDetails(documentContext))
        } catch (e: ExternalException) {
            null
        }
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