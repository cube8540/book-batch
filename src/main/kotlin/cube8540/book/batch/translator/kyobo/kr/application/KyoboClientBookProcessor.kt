package cube8540.book.batch.translator.kyobo.kr.application

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController
import cube8540.book.batch.translator.kyobo.kr.client.KyoboBookClient
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.batch.item.ItemProcessor

class KyoboClientBookProcessor(
    private val client: KyoboBookClient,
    private val documentMapper: BookDocumentMapper
): ItemProcessor<BookDetails, BookDetails> {

    var controller: BookDetailsController = KyoboBookDetailsController()

    override fun process(item: BookDetails): BookDetails {
        val result = client.search(item.isbn)

        val document = Jsoup.parse(result)
            .outputSettings(Document.OutputSettings().prettyPrint(false))

        val documentContext = documentMapper.convertValue(document)
        return controller.merge(item, BookDetails(documentContext))
    }
}