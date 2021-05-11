package cube8540.book.batch.infra.nl.go

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object NationalLibraryAPIDeserializerTestEnvironment {

    internal const val errorResult = "ERROR"
    internal const val errorMessage = "errorMessage0001"
    internal const val errorCode = "errorCode0001"

    internal const val pageNumber = "1"
    internal const val totalCount = "100"

    internal const val responseTitle0 = "responseTitle0000"
    internal const val responseTitle1 = "responseTitle0001"
    internal const val responseTitle2 = "responseTitle0002"

    internal const val title0 = "title0000"
    internal const val title1 = "title0001"
    internal const val title2 = "title0002"

    internal const val isbn0 = "9791136202093"
    internal const val isbn1 = "9791136226259"
    internal const val isbn2 = "9791133421589"

    internal const val setIsbn0 = "9791133421572"

    internal const val additionalCode0 = "07650"

    internal const val setAdditionalCode = "07650"

    internal const val seriesNo = "0"
    internal const val setExpression = "expression0001"

    internal const val responsePublisher = "responsePublisher0001"
    internal const val publisherCode = "publisher0001"
    internal const val subject = "0000"

    internal const val author = "author0000"

    internal const val responseRealPublishDate = "20210503"
    internal val realPublishDate = LocalDate.parse(responseRealPublishDate, DateTimeFormatter.BASIC_ISO_DATE)

    internal const val responsePublishPreDate = "20210101"
    internal val publishPreDate = LocalDate.parse(responsePublishPreDate, DateTimeFormatter.BASIC_ISO_DATE)

    internal const val updateDate = "20210501"
}