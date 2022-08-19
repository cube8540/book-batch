package cube8540.book.batch.translator.kyobo.kr.application

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController
import cube8540.book.batch.translator.client.InternalBadRequestException
import cube8540.book.batch.translator.client.InvalidAuthenticationException
import cube8540.book.batch.translator.kyobo.kr.client.KyoboBookClient
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor

class KyoboClientBookProcessor(
    private val client: KyoboBookClient,
    private val documentMapper: BookDocumentMapper
): ItemProcessor<BookDetails, BookDetails> {

    private val logger = LoggerFactory.getLogger(KyoboClientBookProcessor::class.java)

    var controller: BookDetailsController = KyoboBookDetailsController()

    override fun process(item: BookDetails): BookDetails? {
        val result = client.search(item.isbn)

        return try {
            val document = Jsoup.parse(result)
                .outputSettings(Document.OutputSettings().prettyPrint(false))

            val documentContext = documentMapper.convertValue(document)
            controller.merge(item, BookDetails(documentContext))
        } catch (e: Exception) {
            when (e) {
                is InternalBadRequestException, is InvalidAuthenticationException -> {
                    logger.error("Throws exception during convert book (${item.isbn})", e)
                    null
                } else -> throw e
            }
        }
    }
}