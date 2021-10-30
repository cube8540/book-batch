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
    const val salePrice = "eg:salePrice"
}

object KyoboBookInputNameSelector {
    const val seriesBarcode = "sBarcode"
    const val aBarcode = "aBarcode" // 정확한 풀네임을 모르겠다..
    const val categoryCode = "pt"
}

object KyoboBookClassSelector {
    const val bookContent = ".content_middle > .content_left > .box_detail_content > .box_detail_article"
}

object KyoboBookCommentText {
    const val descriptionCommentText = "***s:책소개***"
    const val indexCommentText = "***s:목차***"
}