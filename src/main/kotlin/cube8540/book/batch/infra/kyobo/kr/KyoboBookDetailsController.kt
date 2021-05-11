package cube8540.book.batch.infra.kyobo.kr

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.external.BookDetailsController

class KyoboBookDetailsController: BookDetailsController {
    override fun merge(base: BookDetails, item: BookDetails): BookDetails {
        base.seriesCode = item.seriesCode
        base.title = item.title

        base.authors = item.authors
        base.divisions = item.divisions

        base.largeThumbnail = item.largeThumbnail
        base.mediumThumbnail = item.mediumThumbnail

        base.description = item.description
        base.price = item.price

        if (base.original != null && item.original != null) {
            base.original = base.original!! + item.original!!
        } else if (item.original != null) {
            base.original = item.original
        }

        return base
    }
}