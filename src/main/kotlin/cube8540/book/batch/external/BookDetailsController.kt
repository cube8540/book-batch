package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetails

interface BookDetailsController {
    fun merge(base: BookDetails, item: BookDetails): BookDetails?
}