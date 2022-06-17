package cube8540.book.batch.translator.aladin.kr

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController

class AladinBookDetailsController: BookDetailsController {
    override fun merge(base: BookDetails, item: BookDetails): BookDetails {
        base.title = item.title
        base.publishDate = item.publishDate
        if (base.original != null && item.original != null) {
            base.original = (base.original!! + item.original!!).toMutableMap()
        } else if (base.original == null) {
            base.original = item.original
        }
        if (base.externalLinks != null && item.externalLinks != null) {
            base.externalLinks = (base.externalLinks!! + item.externalLinks!!).toMutableMap()
        } else if (item.externalLinks != null) {
            base.externalLinks = item.externalLinks
        }
        base.confirmedPublication = true
        return base
    }
}