package cube8540.book.batch.translator.naver.com.application

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.book.domain.*
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponse
import java.net.URI
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NaverBookResponseContext(private val book: NaverBookClientResponse.Book, private val publisherRawMapper: PublisherRawMapper)
    : BookDetailsContext {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
        internal val mappingType = MappingType.NAVER_BOOK

        internal val formatter = DateTimeFormatter.BASIC_ISO_DATE
    }

    override fun extractIsbn(): String = book.isbn?.split(" ")?.let { it ->
        if (it.size >= 2) {
            it[1]
        } else {
            it[0]
        }
    } ?: ""

    override fun extractTitle(): String? = book.title

    override fun extractSeriesCode(): String? = null

    override fun extractSeriesIsbn(): String? = null

    override fun extractDivisions(): Set<String>? = null

    override fun extractPublisher(): String? = book.publisher?.let { publisherRawMapper.mapping(it) }

    override fun extractPublishDate(): LocalDate? = book.publishDate

    override fun extractAuthors(): Set<String>? = null

    override fun extractThumbnail(): Thumbnail = Thumbnail(smallThumbnail = book.image?.let { URI.create(it) })

    override fun extractDescription(): String? = null

    override fun extractIndex(): List<String>? = null

    override fun extractKeywords(): Set<String>? = null

    override fun extractOriginal(): Map<OriginalPropertyKey, String?> = mapOf(
        OriginalPropertyKey(NaverBookClientResponse.isbn, mappingType) to book.isbn,
        OriginalPropertyKey(NaverBookClientResponse.title, mappingType) to book.title,
        OriginalPropertyKey(NaverBookClientResponse.link, mappingType) to book.link,
        OriginalPropertyKey(NaverBookClientResponse.image, mappingType) to book.image,
        OriginalPropertyKey(NaverBookClientResponse.author, mappingType) to book.author,
        OriginalPropertyKey(NaverBookClientResponse.price, mappingType) to book.price?.toString(),
        OriginalPropertyKey(NaverBookClientResponse.discount, mappingType) to book.discount?.toString(),
        OriginalPropertyKey(NaverBookClientResponse.publisher, mappingType) to book.publisher,
        OriginalPropertyKey(NaverBookClientResponse.publishDate, mappingType) to book.publishDate?.format(formatter)
    )

    override fun extractExternalLink(): Map<MappingType, BookExternalLink>? = null

    override fun createdAt(): LocalDateTime = LocalDateTime.now(clock)

    override fun hashCode(): Int = book.hashCode()

    override fun equals(other: Any?): Boolean = when (other) {
        is NaverBookResponseContext -> {
            other.book == book
        }
        else -> false
    }
}