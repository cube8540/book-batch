package cube8540.book.batch.domain

import java.time.LocalDate
import java.time.LocalDateTime

interface BookDetailsContext {
    fun resolveIsbn(): String

    fun resolveTitle(): String?

    fun resolveSeriesCode(): String?

    fun resolveDivisions(): Set<String>?

    fun resolvePublisher(): String?

    fun resolvePublishDate(): LocalDate?

    fun resolveAuthors(): Set<String>?

    fun resolveThumbnail(): Thumbnail?

    fun resolveDescription(): String?

    fun resolveKeywords(): Set<String>?

    fun resolvePrice(): Double?

    fun resolveOriginal(): Map<OriginalPropertyKey, String?>?

    fun createdAt(): LocalDateTime?
}