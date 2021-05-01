package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.OriginalPropertyKey
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime

object BookDetailsPersistCustomRepositoryImplTestEnvironment {

    internal const val isbn = "1234567890123"
    internal const val title = "test book title"
    internal const val seriesCode = "series000000"
    internal const val publisher = "publisher00000"
    internal val publishDate: LocalDate = LocalDate.of(2021, 5, 1)
    internal val largeThumbnail: URI = URI.create("http://localhost/large-thumbnail")
    internal val mediumThumbnail: URI = URI.create("http://localhost/medium-thumbnail")
    internal val smallThumbnail: URI = URI.create("http://localhost/small-thumbnail")
    internal const val description = "description 0000000"
    internal const val price: Double = 30000.0
    internal val createdAt: LocalDateTime = LocalDateTime.of(2021, 5, 1, 21, 55, 0,0)

    internal const val division0 = "division0"
    internal const val division1 = "division1"
    internal const val division2 = "division2"

    internal const val author0 = "author0"
    internal const val author1 = "author1"
    internal const val author2 = "author2"

    internal const val keyword0 = "keyword0"
    internal const val keyword1 = "keyword1"
    internal const val keyword2 = "keyword2"

    internal val propertyKey0 = OriginalPropertyKey("property", MappingType.NAVER_BOOK)
    internal const val propertyValue0= "value0"

    internal val propertyKey1 = OriginalPropertyKey("property", MappingType.NATIONAL_LIBRARY)
    internal const val propertyValue1 = "value1"
}