package cube8540.book.batch.translator.kyobo.kr.application

import cube8540.book.batch.book.domain.BookDetailsContext
import org.jsoup.nodes.Document

interface BookDocumentMapper {

    fun convertValue(document: Document): BookDetailsContext

}