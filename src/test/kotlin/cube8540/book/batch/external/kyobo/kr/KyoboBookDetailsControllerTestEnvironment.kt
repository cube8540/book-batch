package cube8540.book.batch.external.kyobo.kr

import java.net.URI
import java.time.LocalDate

object KyoboBookDetailsControllerTestEnvironment {
    internal const val isbn = "isbn0000"

    internal val mergedDivisions = setOf("division0000", "division0001", "division0002")
    internal val mergedAuthors = setOf("author0000", "author0001", "author0002")

    internal const val mergedTitle = "mergedTitle0000"

    internal val mergedLargeThumbnail = URI.create("http://localhost/merged-large-thumbnail")
    internal val mergedMediumThumbnail = URI.create("http://localhost/merged-medium-thumbnail")

    internal const val mergedDescription = "mergedDesc"
    internal const val mergedPrice = 5000.0
}