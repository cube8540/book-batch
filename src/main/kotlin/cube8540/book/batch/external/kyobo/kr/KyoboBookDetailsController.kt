package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.Thumbnail
import cube8540.book.batch.book.domain.BookDetailsController

class KyoboBookDetailsController: BookDetailsController {
    override fun merge(base: BookDetails, item: BookDetails): BookDetails {
        base.seriesCode = item.seriesCode
        base.title = item.title

        base.authors = item.authors
        base.divisions = item.divisions

        if (base.thumbnail == null) {
            base.thumbnail = Thumbnail(item.thumbnail?.largeThumbnail, item.thumbnail?.mediumThumbnail, null)
        } else {
            base.thumbnail?.largeThumbnail = item.thumbnail?.largeThumbnail
            base.thumbnail?.mediumThumbnail = item.thumbnail?.mediumThumbnail
        }

        base.description = item.description
        base.price = item.price

        if (base.original != null && item.original != null) {
            base.original = (base.original!! + item.original!!).toMutableMap()
        } else if (item.original != null) {
            base.original = item.original
        }

        return base
    }
}