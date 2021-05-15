package cube8540.book.batch.infra.kyobo.kr

import java.net.URI

object KyoboBookDetailsControllerTestEnvironment {
    internal const val isbn = "isbn0000"

    internal const val mergedSeriesCode = "mergedSeriesCode"

    internal val mergedDivisions = setOf("division0000", "division0001", "division0002")
    internal val mergedAuthors = setOf("author0000", "author0001", "author0002")

    internal const val mergedTitle = "mergedTitle0000"

    internal val mergedLargeThumbnail = URI.create("http://localhost/merged-large-thumbnail")
    internal val mergedMediumThumbnail = URI.create("http://localhost/merged-medium-thumbnail")
    internal val mergedSmallThumbnail = URI.create("http://localhost/merged-small-thumbnail")

    internal const val mergedDescription = "mergedDesc"
    internal const val mergedPrice = 5000.0

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
}