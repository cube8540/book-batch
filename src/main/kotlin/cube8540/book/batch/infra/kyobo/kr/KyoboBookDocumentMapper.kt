package cube8540.book.batch.infra.kyobo.kr

import cube8540.book.batch.domain.BookDetailsContext
import cube8540.book.batch.domain.DivisionRawMapper
import cube8540.book.batch.external.BookDocumentMapper
import cube8540.book.batch.external.exception.InternalBadRequestException
import cube8540.book.batch.external.exception.InvalidAuthenticationException
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