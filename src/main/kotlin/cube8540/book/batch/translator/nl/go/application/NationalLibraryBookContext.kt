package cube8540.book.batch.translator.nl.go.application

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.book.domain.*
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.AUTHOR
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.EA_ADD_CODE
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.ISBN
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.PUBLISHER
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.PUBLISH_PREDATE
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.REAL_PUBLISH_DATE
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.SERIES_NO
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.SET_ADD_CODE
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.SET_EXPRESSION
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.SET_ISBN
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.SUBJECT
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.TITLE
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse.Companion.UPDATE_DATE
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.regex.Pattern

class NationalLibraryBookContext(private val book: NationalLibraryClientResponse.Book, private val publisherRawMapper: PublisherRawMapper)
    : BookDetailsContext {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())

        internal const val volumePattern = "\\s\\d+권?\$"
        internal val mappingType = MappingType.NATIONAL_LIBRARY
    }

    override fun extractIsbn(): String = when (book.isbn) {
        null -> {
            book.setIsbn!!
        }
        else -> {
            book.isbn
        }
    }

    override fun extractTitle(): String {
        val volume = book.seriesNo ?: 0
        val originalTitle = book.title
        val title = when {
            volume != 0 -> {
                "${originalTitle.replace(volumePattern, "")} $volume"
            }
            Pattern.compile(volumePattern).matcher(originalTitle).find() -> {
                originalTitle.replace(Regex("권$"), "")
            }
            else -> {
                originalTitle
            }
        }
        return title.trim()
    }

    override fun extractSeriesCode(): String? = null

    override fun extractSeriesIsbn(): String? = book.setIsbn

    override fun extractDivisions(): Set<String>? = null

    override fun extractPublisher(): String? = publisherRawMapper.mapping(book.publisher)

    override fun extractPublishDate(): LocalDate? = when (book.realPublishDate) {
        null -> {
            book.publishPreDate
        }
        else -> {
            book.realPublishDate
        }
    }

    override fun extractAuthors(): Set<String>? = null

    override fun extractThumbnail(): Thumbnail? = null

    override fun extractDescription(): String? = null

    override fun extractIndex(): List<String>? = null

    override fun extractKeywords(): Set<String>? = null

    override fun extractOriginal(): Map<OriginalPropertyKey, String?> = mapOf(
        OriginalPropertyKey(TITLE, mappingType) to book.title,
        OriginalPropertyKey(ISBN, mappingType) to book.isbn,
        OriginalPropertyKey(SET_ISBN, mappingType) to book.setIsbn,
        OriginalPropertyKey(EA_ADD_CODE, mappingType) to book.additionalCode,
        OriginalPropertyKey(SET_ADD_CODE, mappingType) to book.setAdditionalCode,
        OriginalPropertyKey(SERIES_NO, mappingType) to book.seriesNo?.toString(),
        OriginalPropertyKey(SET_EXPRESSION, mappingType) to book.setExpression,
        OriginalPropertyKey(SUBJECT, mappingType) to book.subject,
        OriginalPropertyKey(PUBLISHER, mappingType) to book.publisher,
        OriginalPropertyKey(AUTHOR, mappingType) to book.author,
        OriginalPropertyKey(REAL_PUBLISH_DATE, mappingType) to book.realPublishDate?.toString(),
        OriginalPropertyKey(PUBLISH_PREDATE, mappingType) to book.publishPreDate?.toString(),
        OriginalPropertyKey(UPDATE_DATE, mappingType) to book.updateDate?.toString()
    )

    override fun extractExternalLink(): Map<MappingType, BookExternalLink>? = null

    override fun createdAt(): LocalDateTime? = LocalDateTime.now(clock)

    override fun equals(other: Any?): Boolean = when (other) {
        is NationalLibraryBookContext -> {
            other.book == this.book
        }
        else -> {
            false
        }
    }

    override fun hashCode(): Int = book.hashCode()
}