package cube8540.book.batch.external.naver.com

import java.time.LocalDate

object NaverBookAPIDeserializerTestEnvironment {

    internal const val totalCount = 100
    internal const val display = 10
    internal const val start = 1

    internal const val isbn0 = "9791136202093"
    internal const val responseIsbn0 = "1136215301 $isbn0"
    internal const val isbn1 = "9791136226259"
    internal const val responseIsbn1 = "1136226257 $isbn1"
    internal const val isbn2 = "9791133421589"
    internal const val responseIsbn2 = "1164120123 $isbn2"

    internal const val link = "http://localhost/link0000"
    internal const val image = "http://localhost/image0000"
    internal const val author = "author0000"

    internal const val title0 = "title0000"
    internal const val title1 = "title0001"
    internal const val title2 = "title0002"

    internal const val publisher = "publisher0000"
    internal const val publisherCode = "publisherCode0001"

    internal const val price = 6500
    internal const val discount = 4500

    internal const val responsePublishDate = "20210502"
    internal val publishDate: LocalDate = LocalDate.of(2021, 5, 2)

    internal const val description = "description0000"

    internal const val errorCode = "errorCode0001"
    internal const val errorMessage = "errorMessage0001"

}