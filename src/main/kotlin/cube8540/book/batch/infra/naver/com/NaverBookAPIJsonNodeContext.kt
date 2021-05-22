package cube8540.book.batch.infra.naver.com

import com.fasterxml.jackson.databind.JsonNode
import cube8540.book.batch.BatchApplication
import cube8540.book.batch.domain.*
import java.net.URI
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NaverBookAPIJsonNodeContext(private val jsonNode: JsonNode, private val publisherRawMapper: PublisherRawMapper): BookDetailsContext {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
        internal val mappingType = MappingType.NAVER_BOOK
    }

    override fun resolveIsbn(): String = jsonNode.get(NaverBookAPIResponseNames.isbn).asText().split(" ")[1]

    override fun resolveTitle(): String? = jsonNode.get(NaverBookAPIResponseNames.title)?.asText()

    override fun resolveSeriesCode(): String? = null

    override fun resolveDivisions(): Set<String>? = null

    override fun resolvePublisher(): String? = publisherRawMapper
        .mapping(jsonNode.get(NaverBookAPIResponseNames.publisher).asText())

    override fun resolvePublishDate(): LocalDate? = jsonNode.get(NaverBookAPIResponseNames.publishDate)
        ?.let { LocalDate.parse(it.asText(), DateTimeFormatter.BASIC_ISO_DATE) }

    override fun resolveAuthors(): Set<String>? = null

    override fun resolveThumbnail(): Thumbnail = Thumbnail(
        smallThumbnail = jsonNode.get(NaverBookAPIResponseNames.image)?.let { URI.create(it.asText()) },
        largeThumbnail = null,
        mediumThumbnail = null
    )

    override fun resolveDescription(): String? = null

    override fun resolveKeywords(): Set<String>? = null

    override fun resolvePrice(): Double?  = null

    override fun resolveOriginal(): Map<OriginalPropertyKey, String?> {
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