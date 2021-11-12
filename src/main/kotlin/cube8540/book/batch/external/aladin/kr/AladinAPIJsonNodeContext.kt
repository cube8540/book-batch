package cube8540.book.batch.external.aladin.kr

import com.fasterxml.jackson.databind.JsonNode
import cube8540.book.batch.BatchApplication
import cube8540.book.batch.book.domain.*
import java.net.URI
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

    override fun extractIsbn(): String = jsonNode.get(AladinAPIResponseNames.isbn).asText()

    override fun extractTitle(): String? = jsonNode.get(AladinAPIResponseNames.title).asText()

    override fun extractSeriesCode(): String? = null

    override fun extractSeriesIsbn(): String? = null

    override fun extractDivisions(): Set<String>? = null

    override fun extractPublisher(): String? = jsonNode.get(AladinAPIResponseNames.publisher)?.asText()
        ?.let { publisherRawMapper.mapping(it) }

    override fun extractPublishDate(): LocalDate? = jsonNode.get(AladinAPIResponseNames.publishDate)?.asText()
        ?.let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }

    override fun extractAuthors(): Set<String>? {
        val node = jsonNode.get(AladinAPIResponseNames.author)?.asText()

        return if (node?.let { it.isNotEmpty() && it.isNotBlank() } == true) {
            node.split(", ").toSet()
        } else {
            null
        }
    }

    override fun extractThumbnail(): Thumbnail? = null

    override fun extractDescription(): String? = null

    override fun extractIndex(): List<String>? = null

    override fun extractKeywords(): Set<String>? = null

    override fun extractOriginal(): Map<OriginalPropertyKey, String?> {
        val map = HashMap<OriginalPropertyKey, String?>()
        map[OriginalPropertyKey(AladinAPIResponseNames.isbn, mappingType)] = extractIsbn()
        map[OriginalPropertyKey(AladinAPIResponseNames.title, mappingType)] = extractTitle()

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

    override fun extractExternalLink(): Map<MappingType, BookExternalLink> {
        val uri = jsonNode.get(AladinAPIResponseNames.link).let { URI.create(it.asText()) }
        val originalPrice = jsonNode.get(AladinAPIResponseNames.originalPrice)?.asDouble()
        val salePrice = jsonNode.get(AladinAPIResponseNames.salePrice)?.asDouble()

        return mapOf(MappingType.ALADIN to BookExternalLink(uri, originalPrice, salePrice))
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