package cube8540.book.batch.translator.kyobo.kr.application

import cube8540.book.batch.book.domain.BookDetailsContext
import cube8540.book.batch.book.domain.DivisionRawMapper
import cube8540.book.batch.translator.kyobo.kr.client.KyoboBookMetaTagPropertySelector
import cube8540.book.batch.translator.client.InternalBadRequestException
import cube8540.book.batch.translator.client.InvalidAuthenticationException
import org.jsoup.nodes.Document

class KyoboBookDocumentMapper(private val divisionRawMapper: DivisionRawMapper): BookDocumentMapper {

    override fun convertValue(document: Document): BookDetailsContext {
        val metaTags = document.getElementsByTag("meta")

        if (metaTags.none { it.attr("property").equals(KyoboBookMetaTagPropertySelector.originalBarcode) }) {
            throw InternalBadRequestException("requested isbn is not found")
        }
        if (metaTags.none { it.attr("property").equals(KyoboBookMetaTagPropertySelector.isbn) }) {
            throw InvalidAuthenticationException("login info is invalid")
        }

        return KyoboBookJsoupDocumentContext(document, divisionRawMapper)
    }
}