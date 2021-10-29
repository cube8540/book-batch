package cube8540.book.batch.external.aladin.kr

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController

class AladinBookDetailsController: BookDetailsController {
    override fun merge(base: BookDetails, item: BookDetails): BookDetails {
        base.title = item.title
        if (base.original != null && item.original != null) {
            base.original = (base.original!! + item.original!!).toMutableMap()
        } else if (base.original == null) {
            base.original = item.original
        }
        return base
    }
}