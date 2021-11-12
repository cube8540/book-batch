package cube8540.book.batch.book.domain

import java.time.LocalDate
import java.time.LocalDateTime

interface BookDetailsContext {
    fun extractIsbn(): String

    fun extractTitle(): String?

    fun extractSeriesCode(): String?

    fun extractSeriesIsbn(): String?

    fun extractDivisions(): Set<String>?

    fun extractPublisher(): String?

    fun extractPublishDate(): LocalDate?

    fun extractAuthors(): Set<String>?

    fun extractThumbnail(): Thumbnail?

    fun extractDescription(): String?

    fun extractIndex(): List<String>?

    fun extractKeywords(): Set<String>?

    fun extractOriginal(): Map<OriginalPropertyKey, String?>?

    fun extractExternalLink(): Map<MappingType, BookExternalLink>?

    fun createdAt(): LocalDateTime?
}