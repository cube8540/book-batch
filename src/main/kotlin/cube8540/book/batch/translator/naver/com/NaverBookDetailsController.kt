package cube8540.book.batch.translator.naver.com

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.Thumbnail
import cube8540.book.batch.book.domain.BookDetailsController

class NaverBookDetailsController: BookDetailsController {
    override fun merge(base: BookDetails, item: BookDetails): BookDetails {
        base.title = item.title
        base.publisher = item.publisher
        base.publishDate = item.publishDate

        if (base.thumbnail == null) {
            base.thumbnail = Thumbnail(null, null, item.thumbnail?.smallThumbnail)
        } else {
            base.thumbnail?.smallThumbnail = item.thumbnail?.smallThumbnail
        }

        if (base.original != null && item.original != null) {
            base.original = (base.original!! + item.original!!).toMutableMap()
        } else if (item.original != null) {
            base.original = item.original
        }
        base.confirmedPublication = true

        return base
    }
}