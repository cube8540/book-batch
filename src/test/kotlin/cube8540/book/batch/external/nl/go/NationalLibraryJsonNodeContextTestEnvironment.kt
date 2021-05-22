package cube8540.book.batch.external.nl.go

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object NationalLibraryJsonNodeContextTestEnvironment {
    internal const val title = "권 1 권 권 권권권"
    internal const val titleWithVolume = "$title 1"
    internal const val titleWithVolumeAndText = "$title 1권"
    internal const val volume = 1
    internal const val expectedTitle = "$title $volume"

    internal const val isbn0 = "9791136202093"
    internal const val isbn1 = "9791136226259"

    internal const val additionalCode0 = "07650"

    internal const val setAdditionalCode = "07650"

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