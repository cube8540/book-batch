package cube8540.book.batch.translator.kyobo.kr.application

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsController
import cube8540.book.batch.book.domain.Thumbnail

class KyoboBookDetailsController: BookDetailsController {
    override fun merge(base: BookDetails, item: BookDetails): BookDetails {
        base.seriesCode = item.seriesCode
        base.title = item.title

        base.authors = item.authors
        base.divisions = item.divisions

        item.publishDate?.let { base.publishDate = it }

        if (base.thumbnail == null) {
            base.thumbnail = Thumbnail(item.thumbnail?.largeThumbnail, item.thumbnail?.mediumThumbnail, null)
        } else {
            base.thumbnail?.largeThumbnail = item.thumbnail?.largeThumbnail
            base.thumbnail?.mediumThumbnail = item.thumbnail?.mediumThumbnail
        }

        base.description = item.description
        base.indexes = item.indexes

        if (base.original != null && item.original != null) {
            base.original = (base.original!! + item.original!!).toMutableMap()
        } else if (item.original != null) {
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