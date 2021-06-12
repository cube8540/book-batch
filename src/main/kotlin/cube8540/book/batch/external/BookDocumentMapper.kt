package cube8540.book.batch.external

import cube8540.book.batch.book.domain.BookDetailsContext
import org.jsoup.nodes.Document

interface BookDocumentMapper {

    fun convertValue(document: Document): BookDetailsContext

}