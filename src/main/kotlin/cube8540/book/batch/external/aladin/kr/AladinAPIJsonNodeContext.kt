package cube8540.book.batch.external.aladin.kr

import com.fasterxml.jackson.databind.JsonNode
import cube8540.book.batch.BatchApplication
import cube8540.book.batch.book.domain.*
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AladinAPIJsonNodeContext(private val jsonNode: JsonNode, private val publisherRawMapper: PublisherRawMapper):
    BookDetailsContext {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
        internal val mappingType = MappingType.ALADIN
    }

    override fun resolveIsbn(): String = jsonNode.get(AladinAPIResponseNames.isbn).asText()

    override fun resolveTitle(): String? = jsonNode.get(AladinAPIResponseNames.title).asText()

    override fun resolveSeriesCode(): String? = null

    override fun resolveSeriesIsbn(): String? = null

    override fun resolveDivisions(): Set<String>? = null

    override fun resolvePublisher(): String? = jsonNode.get(AladinAPIResponseNames.publisher)?.asText()
        ?.let { publisherRawMapper.mapping(it) }

    override fun resolvePublishDate(): LocalDate? = jsonNode.get(AladinAPIResponseNames.publishDate)?.asText()
        ?.let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }

    override fun resolveAuthors(): Set<String>? {
        val node = jsonNode.get(AladinAPIResponseNames.author)?.asText()

        return if (node?.let { it.isNotEmpty() && it.isNotBlank() } == true) {
            node.split(", ").toSet()
        } else {
            null
        }
    }

    override fun resolveThumbnail(): Thumbnail? = null

    override fun resolveDescription(): String? = null

    override fun resolveIndex(): List<String>? = null

    override fun resolveKeywords(): Set<String>? = null

    override fun resolveOriginal(): Map<OriginalPropertyKey, String?> {
        val map = HashMap<OriginalPropertyKey, String?>()
        map[OriginalPropertyKey(AladinAPIResponseNames.isbn, mappingType)] = resolveIsbn()
        map[OriginalPropertyKey(AladinAPIResponseNames.title, mappingType)] = resolveTitle()

        map[OriginalPropertyKey(AladinAPIResponseNames.categoryId, mappingType)] =
            jsonNode.get(AladinAPIResponseNames.categoryId)?.asText()
        map[OriginalPropertyKey(AladinAPIResponseNames.publishDate, mappingType)] =
            jsonNode.get(AladinAPIResponseNames.publishDate)?.asText()
        map[OriginalPropertyKey(AladinAPIResponseNames.author, mappingType)] =
            jsonNode.get(AladinAPIResponseNames.author)?.asText()
        map[OriginalPropertyKey(AladinAPIResponseNames.link, mappingType)] =
            jsonNode.get(AladinAPIResponseNames.link)?.asText()
        map[OriginalPropertyKey(AladinAPIResponseNames.publisher, mappingType)] =
            jsonNode.get(AladinAPIResponseNames.publisher)?.asText()
        return map
    }

    override fun createdAt(): LocalDateTime? = LocalDateTime.now(clock)

    override fun equals(other: Any?): Boolean = when (other) {
        null -> {
            false
        }
        is AladinAPIJsonNodeContext -> {
            other.jsonNode == this.jsonNode
        }
        else -> {
            false
        }
    }

    override fun hashCode(): Int = jsonNode.hashCode()
}