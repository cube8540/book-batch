package cube8540.book.batch.external.kyobo.kr

object KyoboBookMetaTagNameSelector {
    const val author = "author"
}

object KyoboBookMetaTagPropertySelector {
    const val isbn = "eg:itemId"
    const val originalBarcode = "og:barcode"
    const val title = "eg:itemName"
    const val largeThumbnail = "og:image"
    const val mediumThumbnail = "eg:itemImage"
    const val originalPrice = "eg:originalPrice"
}

object KyoboBookInputNameSelector {
    const val seriesBarcode = "sBarcode"
    const val categoryCode = "pt"
}

object KyoboBookClassSelector {
    const val description = ".content_middle > .content_left > .box_detail_content > .box_detail_article"
}