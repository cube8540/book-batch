package cube8540.book.batch.external

import cube8540.book.batch.domain.BookDetails

interface BookDetailsController {
    fun merge(base: BookDetails, item: BookDetails): BookDetails?
}

class DefaultBookDetailsController: BookDetailsController {
    override fun merge(base: BookDetails, item: BookDetails): BookDetails {
        base.title = item.title
        base.publisher = item.publisher
        base.publishDate = item.publishDate
        base.largeThumbnail = item.largeThumbnail
        base.mediumThumbnail = item.mediumThumbnail
        base.smallThumbnail = item.smallThumbnail
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

class ReturnsNullBookDetailsController: BookDetailsController {
    override fun merge(base: BookDetails, item: BookDetails): BookDetails? = null
}