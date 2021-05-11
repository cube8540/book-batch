package cube8540.book.batch.infra.naver.com

import java.net.URI
import java.time.LocalDate

object NaverBookDetailsControllerTestEnvironment {
    internal const val isbn = "isbn0000"

    internal const val itemOriginalProperty0000 = "originalProperty0000"
    internal const val itemOriginalProperty0001 = "originalProperty0001"
    internal const val itemOriginalProperty0002 = "originalProperty0002"
    internal const val itemOriginalValue0000 = "itemOriginalValue0000"
    internal const val itemOriginalValue0001 = "itemOriginalValue0001"
    internal const val itemOriginalValue0002 = "itemOriginalValue0002"

    internal const val existsOriginalProperty0000 = "existsOriginalProperty0000"
    internal const val existsOriginalProperty0001 = "existsOriginalProperty0001"
    internal const val existsOriginalProperty0002 = "existsOriginalProperty0002"
    internal const val existsOriginalValue0000 = "existsOriginalValue0000"
    internal const val existsOriginalValue0001 = "existsOriginalValue0001"
    internal const val existsOriginalValue0002 = "existsOriginalValue0002"

    internal const val mergedTitle = "mergedTitle0000"
    internal const val mergedPublisher = "mergedPublisher0000"
    internal val mergedPublishDate = LocalDate.of(2021, 5, 9)
    internal val mergedLargeThumbnail = URI.create("http://localhost/merged-large-thumbnail")
    internal val mergedMediumThumbnail = URI.create("http://localhost/merged-medium-thumbnail")
    internal val mergedSmallThumbnail = URI.create("http://localhost/merged-small-thumbnail")
}