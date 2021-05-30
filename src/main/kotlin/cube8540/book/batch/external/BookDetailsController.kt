package cube8540.book.batch.external

import cube8540.book.batch.book.domain.BookDetails

interface BookDetailsController {
    fun merge(base: BookDetails, item: BookDetails): BookDetails?
}