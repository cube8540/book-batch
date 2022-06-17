package cube8540.book.batch.translator.aladin.kr.application

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.book.domain.*
import cube8540.book.batch.translator.aladin.kr.client.AladinBookClientResponse
import java.net.URI
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AladinBookResponseContext(private val book: AladinBookClientResponse.Book, private val publisherRawMapper: PublisherRawMapper)
    : BookDetailsContext {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
        internal val mappingType = MappingType.ALADIN

        internal val formatter = DateTimeFormatter.ISO_DATE
    }

    override fun extractIsbn(): String = book.isbn13

    override fun extractTitle(): String? = book.title

    override fun extractSeriesCode(): String? = null

    override fun extractSeriesIsbn(): String? = null

    override fun extractDivisions(): Set<String>? = null

    override fun extractPublisher(): String? = book.publisher?.let { publisherRawMapper.mapping(it) }

    override fun extractPublishDate(): LocalDate? = book.publishDate

    override fun extractAuthors(): Set<String>? {
        return if (book.author?.let { it.isNotEmpty() && it.isNotBlank() } == true) {
            book.author.split(", ").toSet()
        } else {
            null
        }
    }

    override fun extractThumbnail(): Thumbnail? = null

    override fun extractDescription(): String? = null

    override fun extractIndex(): List<String>? = null

    override fun extractKeywords(): Set<String>? = null

    override fun extractOriginal(): Map<OriginalPropertyKey, String?> = mapOf(
        OriginalPropertyKey(AladinBookClientResponse.TITLE, mappingType) to book.title,
        OriginalPropertyKey(AladinBookClientResponse.AUTHOR, mappingType) to book.author,
        OriginalPropertyKey(AladinBookClientResponse.PUBLISHER, mappingType) to book.publisher,
        OriginalPropertyKey(AladinBookClientResponse.PUBLISH_DATE, mappingType) to book.publishDate?.format(formatter),
        OriginalPropertyKey(AladinBookClientResponse.ISBN, mappingType) to book.isbn,
        OriginalPropertyKey(AladinBookClientResponse.ISBN_13, mappingType) to book.isbn13,
        OriginalPropertyKey(AladinBookClientResponse.PRICE_STANDARD, mappingType) to book.priceStandard?.toString(),
        OriginalPropertyKey(AladinBookClientResponse.PRICE_SALES, mappingType) to book.priceStandard?.toString(),
        OriginalPropertyKey(AladinBookClientResponse.CATEGORY_ID, mappingType) to book.categoryId?.toString(),
        OriginalPropertyKey(AladinBookClientResponse.LINK, mappingType) to book.link
    )

    override fun extractExternalLink(): Map<MappingType, BookExternalLink>? {
        val uri = book.link?.let { URI.create(it) }
        val originalPrice = book.priceStandard?.toDouble()
        val salePrice = book.priceSales?.toDouble()

        return uri?.let { mapOf(mappingType to BookExternalLink(it, originalPrice, salePrice)) }
    }

    override fun createdAt(): LocalDateTime = LocalDateTime.now(clock)

    override fun equals(other: Any?): Boolean = when (other) {
        is AladinBookResponseContext -> {
            other.book == this.book
        }
        else -> {
            false
        }
    }

    override fun hashCode(): Int = book.hashCode()
}