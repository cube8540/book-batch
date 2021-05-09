package cube8540.book.batch.external.nl.go

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.external.BookDetailsController

class NationalLibraryBookDetailsController: BookDetailsController {
    override fun merge(base: BookDetails, item: BookDetails): BookDetails? = null
}