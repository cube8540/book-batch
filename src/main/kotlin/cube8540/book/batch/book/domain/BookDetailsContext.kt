package cube8540.book.batch.book.domain

import java.time.LocalDate
import java.time.LocalDateTime

interface BookDetailsContext {
    fun resolveIsbn(): String

    fun resolveTitle(): String?

    fun resolveSeriesCode(): String?

    fun resolveSeriesIsbn(): String?

    fun resolveDivisions(): Set<String>?

    fun resolvePublisher(): String?

    fun resolvePublishDate(): LocalDate?

    fun resolveAuthors(): Set<String>?

    fun resolveThumbnail(): Thumbnail?

    fun resolveDescription(): String?

    fun resolveIndex(): List<String>?

    fun resolveKeywords(): Set<String>?

    fun resolveOriginal(): Map<OriginalPropertyKey, String?>?

    fun resolveExternalLink(): Map<MappingType, BookExternalLink>?

    fun createdAt(): LocalDateTime?
}