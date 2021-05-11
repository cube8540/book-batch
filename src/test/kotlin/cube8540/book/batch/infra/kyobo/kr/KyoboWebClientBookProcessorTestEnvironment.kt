package cube8540.book.batch.infra.kyobo.kr

import java.net.URI

object KyoboWebClientBookProcessorTestEnvironment {

    const val isbn = "9791133447831"
    const val title = "title0000"

    const val responseBody = "<responseBody></responseBody>"

    val author = setOf("author 0001", "author 0002")

    val largeThumbnail: URI = URI.create("http://localhost/largeThubmnail")
    val mediumThumbnail: URI = URI.create("http://localhost/mediumThumbnail")

    const val originalPrice = 5000.0

    const val seriesBarcode = "seriesBarcode00001"

    val categories = setOf("category00", "category01", "category02")

    const val description = "description0000001"
}