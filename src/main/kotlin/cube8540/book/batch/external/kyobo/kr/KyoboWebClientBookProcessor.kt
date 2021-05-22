package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.external.BookDetailsController
import cube8540.book.batch.external.BookDocumentMapper
import cube8540.book.batch.external.exception.ExternalException
import org.jsoup.Jsoup
import org.springframework.batch.item.ItemProcessor
import org.springframework.web.reactive.function.client.WebClient
import reactor.util.retry.Retry
import java.time.Duration

class KyoboWebClientBookProcessor(
    private val webClient: WebClient,
    private val documentMapper: BookDocumentMapper
): ItemProcessor<BookDetails, BookDetails> {

    companion object {
        const val defaultRetryCount = 1
        const val defaultRetryDelaySecond = 5
    }

    var controller: BookDetailsController = KyoboBookDetailsController()

    var retryCount: Int = defaultRetryCount
    var retryDelaySecond: Int = defaultRetryDelaySecond

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
        .retryWhen(Retry.fixedDelay(retryCount.toLong(), Duration.ofSeconds(retryDelaySecond.toLong())))
        .block()
}