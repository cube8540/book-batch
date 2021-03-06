package cube8540.book.batch.external.nl.go

import com.fasterxml.jackson.databind.JsonNode
import cube8540.book.batch.BatchApplication
import cube8540.book.batch.book.domain.*
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class NationalLibraryJsonNodeContext(private val jsonNode: JsonNode, private val publisherRawMapper: PublisherRawMapper):
    BookDetailsContext {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())

        internal const val volumePattern = "\\s\\d+권?\$"
        internal val mappingType = MappingType.NATIONAL_LIBRARY
    }

    override fun extractIsbn(): String {
        val isbnNode = jsonNode.get(NationalLibraryAPIResponseNames.isbn)
        val setIsbnNode = jsonNode.get(NationalLibraryAPIResponseNames.setIsbn)

        return when {
            isbnNode == null || isbnNode.asText().isEmpty() -> {
                setIsbnNode.asText()
            }
            else -> {
                isbnNode.asText()
            }
        }
    }

    override fun extractTitle(): String {
        val volume = jsonNode.get(NationalLibraryAPIResponseNames.seriesNo)?.asText() ?: 0
        val originalTitle = jsonNode.get(NationalLibraryAPIResponseNames.title).asText()
        val title = when {
            volume != 0 -> {
                "${originalTitle.replace(Regex(volumePattern), "")} $volume"
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

    override fun extractSeriesIsbn(): String? {
        val isbn = jsonNode.get(NationalLibraryAPIResponseNames.setIsbn)?.asText()
        return if (isbn != null && isbn.isNotEmpty()) {
            isbn
        } else {
            null
        }
    }

    override fun extractDivisions(): Set<String>? = null

    override fun extractPublisher(): String? = publisherRawMapper
        .mapping(jsonNode.get(NationalLibraryAPIResponseNames.publisher).asText())

    override fun extractPublishDate(): LocalDate? {
        val realPublishDateNode = jsonNode.get(NationalLibraryAPIResponseNames.realPublishDate)
        val publishPreDateNode = jsonNode.get(NationalLibraryAPIResponseNames.publishPreDate)
        return when {
            realPublishDateNode == null || realPublishDateNode.asText().isEmpty() -> {
                LocalDate.parse(publishPreDateNode.asText(), DateTimeFormatter.BASIC_ISO_DATE)
            }
            else -> {
                LocalDate.parse(realPublishDateNode.asText(), DateTimeFormatter.BASIC_ISO_DATE)
            }
        }
    }

    override fun extractAuthors(): Set<String>?  = null

    override fun extractThumbnail(): Thumbnail?  = null

    override fun extractDescription(): String?  = null

    override fun extractIndex(): List<String>? = null

    override fun extractKeywords(): Set<String>?  = null

    override fun extractOriginal(): Map<OriginalPropertyKey, String?> {
        val map = HashMap<OriginalPropertyKey, String?>()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.isbn, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.isbn)?.asText()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.title, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.title)?.asText()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.publisher, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.publisher)?.asText()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.realPublishDate, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.realPublishDate)?.asText()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.publishPreDate, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.publishPreDate)?.asText()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.setIsbn, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.setIsbn)?.asText()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.additionalCode, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.additionalCode)?.asText()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.setAdditionalCode, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.setAdditionalCode)?.asText()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.seriesNo, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.seriesNo)?.asText()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.setExpression, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.setExpression)?.asText()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.subject, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.subject)?.asText()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.author, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.author)?.asText()
        map[OriginalPropertyKey(NationalLibraryAPIResponseNames.updateDate, mappingType)] =
            jsonNode.get(NationalLibraryAPIResponseNames.updateDate)?.asText()
        return map
    }

    override fun extractExternalLink(): Map<MappingType, BookExternalLink>? = null

    override fun createdAt(): LocalDateTime = LocalDateTime.now(clock)

    override fun equals(other: Any?): Boolean = when (other) {
        null -> {
            false
        }
        is NationalLibraryJsonNodeContext -> {
            other.jsonNode == this.jsonNode
        }
        else -> {
            false
        }
    }

    override fun hashCode(): Int = jsonNode.hashCode()
}