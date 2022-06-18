package cube8540.book.batch.translator.nl.go

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController

class NationalLibraryBookDetailsController: BookDetailsController {
    override fun merge(base: BookDetails, item: BookDetails): BookDetails {
        base.seriesIsbn = item.seriesIsbn
        if (base.original != null && item.original != null) {
            base.original = (base.original!! + item.original!!).toMutableMap()
        } else if (item.original != null) {
            base.original = item.original
        }
        return base
    }
}