package cube8540.book.batch.external.naver.com

import com.fasterxml.jackson.databind.JsonNode
import cube8540.book.batch.BatchApplication
import cube8540.book.batch.book.domain.*
import java.net.URI
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NaverBookAPIJsonNodeContext(private val jsonNode: JsonNode, private val publisherRawMapper: PublisherRawMapper):
    BookDetailsContext {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
        internal val mappingType = MappingType.NAVER_BOOK
    }

    override fun extractIsbn(): String {
        val node = jsonNode.get(NaverBookAPIResponseNames.isbn) ?: return ""
        val texts = node.asText().split(" ")
        return if (texts.size >= 2) {
            texts[1]
        } else {
            ""
        }
    }

    override fun extractTitle(): String? = jsonNode.get(NaverBookAPIResponseNames.title)?.asText()

    override fun extractSeriesCode(): String? = null

    override fun extractSeriesIsbn(): String? = null

    override fun extractDivisions(): Set<String>? = null

    override fun extractPublisher(): String? = publisherRawMapper
        .mapping(jsonNode.get(NaverBookAPIResponseNames.publisher).asText())

    override fun extractPublishDate(): LocalDate? = jsonNode.get(NaverBookAPIResponseNames.publishDate)
        ?.let { LocalDate.parse(it.asText(), DateTimeFormatter.BASIC_ISO_DATE) }

    override fun extractAuthors(): Set<String>? = null

    override fun extractThumbnail(): Thumbnail = Thumbnail(
        smallThumbnail = jsonNode.get(NaverBookAPIResponseNames.image)?.let { URI.create(it.asText()) },
        largeThumbnail = null,
        mediumThumbnail = null
    )

    override fun extractDescription(): String? = null

    override fun extractIndex(): List<String>? = null

    override fun extractKeywords(): Set<String>? = null

    override fun extractOriginal(): Map<OriginalPropertyKey, String?> {
        val map = HashMap<OriginalPropertyKey, String?>()
        map[OriginalPropertyKey(NaverBookAPIResponseNames.isbn, mappingType)] =
            jsonNode.get(NaverBookAPIResponseNames.isbn)?.asText()
        map[OriginalPropertyKey(NaverBookAPIResponseNames.title, mappingType)] =
            jsonNode.get(NaverBookAPIResponseNames.title)?.asText()
        map[OriginalPropertyKey(NaverBookAPIResponseNames.link, mappingType)] =
            jsonNode.get(NaverBookAPIResponseNames.link)?.asText()
        map[OriginalPropertyKey(NaverBookAPIResponseNames.image, mappingType)] =
            jsonNode.get(NaverBookAPIResponseNames.image)?.asText()
        map[OriginalPropertyKey(NaverBookAPIResponseNames.author, mappingType)] =
            jsonNode.get(NaverBookAPIResponseNames.author)?.asText()
        map[OriginalPropertyKey(NaverBookAPIResponseNames.price, mappingType)] =
            jsonNode.get(NaverBookAPIResponseNames.price)?.asText()
        map[OriginalPropertyKey(NaverBookAPIResponseNames.discount, mappingType)] =
            jsonNode.get(NaverBookAPIResponseNames.discount)?.asText()
        map[OriginalPropertyKey(NaverBookAPIResponseNames.publisher, mappingType)] =
            jsonNode.get(NaverBookAPIResponseNames.publisher)?.asText()
        map[OriginalPropertyKey(NaverBookAPIResponseNames.publishDate, mappingType)] =
            jsonNode.get(NaverBookAPIResponseNames.publishDate)?.asText()
        return map
    }

    override fun extractExternalLink(): Map<MappingType, BookExternalLink>? = null

    override fun createdAt(): LocalDateTime = LocalDateTime.now(clock)

    override fun equals(other: Any?): Boolean = when (other) {
        null -> {
            false
        }
        is NaverBookAPIJsonNodeContext -> {
            other.jsonNode == this.jsonNode
        }
        else -> {
            false
        }
    }

    override fun hashCode(): Int = jsonNode.hashCode()
}