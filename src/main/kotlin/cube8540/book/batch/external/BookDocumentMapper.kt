package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetails
import org.jsoup.nodes.Document

interface BookDocumentMapper {

    fun convertValue(document: Document): BookDetails

}