package cube8540.book.batch.external.nl.go

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.external.BookDetailsController

class NationalLibraryBookDetailsController: BookDetailsController {
    override fun merge(base: BookDetails, item: BookDetails): BookDetails {
        base.seriesIsbn = item.seriesIsbn
        if (base.original != null && item.original != null) {
            base.original = base.original!! + item.original!!
        } else if (item.original != null) {
            base.original = item.original
        }
        return base
    }
}