package cube8540.book.batch.infra.kyobo.kr

object KyoboBookJsoupDocumentContextTestEnvironment {

    const val isbn = "9791133447831"
    const val originalBarcode = "og:9791133447831"
    const val title = "title0000"

    const val responseAuthor = "author 0001 , author 0002 "
    val author = setOf("author 0001", "author 0002")

    const val largeThumbnail = "http://localhost/largeThubmnail"
    const val mediumThumbnail = "http://localhost/mediumThumbnail"

    const val originalPrice = "5000"

    const val seriesBarcode = "seriesBarcode00001"
    const val secondSeriesBarcode = "secondSeriesBarcode000001"

    const val categoryDepth0 = "01"
    const val categoryDepth1 = "0101"
    const val categoryDepth2 = "010101"

    const val categoryDepthCode0 = "category00"
    const val categoryDepthCode1 = "category01"
    const val categoryDepthCode2 = "category02"

    const val responseCategoryCode = categoryDepth2

    const val description = "description0000001"

}