package cube8540.book.batch.book.domain

import io.github.cube8540.validator.core.Operator
import io.mockk.every
import io.mockk.mockk
import java.net.URI
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

const val defaultIsbn = "9791133447831"
const val defaultTitle = "title00000"

const val defaultSeriesCode = "seriesCode00000"
const val defaultSeriesIsbn = "seriesIsbn00000"

const val defaultPublisher = "publisher000001"
val defaultPublishDate = LocalDate.of(2021, 6, 5)

val defaultLargeThumbnail: URI = URI.create("http://large-thumbnail")
val defaultMediumThumbnail: URI = URI.create("http://mediumThubmnail")
val defaultSmallThumbnail: URI = URI.create("http://small-thumbnail")
val defaultDivisions = emptySet<String>()
val defaultAuthors = emptySet<String>()
val defaultKeywords = emptySet<String>()
val defaultDescription = "description000000"
val defaultPrice = 5000.0

val defaultBookOriginalFilterId = UUID.randomUUID().toString().replace("-", "")
val defaultBookOriginalFilterIdGenerator: BookDetailsFilterIdGenerator = mockk(relaxed = true) {
    every { generate() } returns defaultBookOriginalFilterId
}

val defaultBookOriginalMappingType: MappingType = MappingType.values()[Random.nextInt(MappingType.values().size)]

val defaultOriginal = emptyMap<OriginalPropertyKey, String>()

fun createBookContext(
    isbn: String = defaultIsbn,
    title: String? = defaultTitle,
    publisher: String? = defaultPublisher,
    publishDate: LocalDate? = defaultPublishDate,
    seriesCode: String? = defaultSeriesCode,
    seriesIsbn: String? = defaultSeriesIsbn,
    largeThumbnail: URI? = defaultLargeThumbnail,
    mediumThumbnail: URI? = defaultMediumThumbnail,
    smallThumbnail: URI? = defaultSmallThumbnail,
    divisions: Set<String>? = defaultDivisions,
    authors: Set<String>? = defaultAuthors,
    keywords: Set<String>? = defaultKeywords,
    description: String? = defaultDescription,
    price: Double? = defaultPrice,
    original: Map<OriginalPropertyKey, String>? = defaultOriginal
): BookDetailsContext {
    val context: BookDetailsContext = mockk(relaxed = true)

    every { context.resolveIsbn() } returns isbn
    every { context.resolveTitle() } returns title
    every { context.resolvePublisher() } returns publisher
    every { context.resolvePublishDate() } returns publishDate
    every { context.resolveSeriesCode() } returns seriesCode
    every { context.resolveSeriesIsbn() } returns seriesIsbn

    if (largeThumbnail != null || mediumThumbnail != null || smallThumbnail != null) {
        every { context.resolveThumbnail() } returns Thumbnail(largeThumbnail, mediumThumbnail, smallThumbnail)
    } else {
        every { context.resolveThumbnail() } returns null
    }

    every { context.resolveDivisions() } returns divisions
    every { context.resolveAuthors() } returns authors
    every { context.resolveKeywords() } returns keywords
    every { context.resolveDescription() } returns description
    every { context.resolvePrice() } returns price

    every { context.resolveOriginal() } returns original

    return context
}

fun createBookDetails(
    isbn: String = defaultIsbn,
    title: String? = defaultTitle,
    publisher: String? = defaultPublisher,
    publishDate: LocalDate? = defaultPublishDate,
    seriesCode: String? = defaultSeriesCode,
    seriesIsbn: String? = defaultSeriesIsbn,
    largeThumbnail: URI? = defaultLargeThumbnail,
    mediumThumbnail: URI? = defaultMediumThumbnail,
    smallThumbnail: URI? = defaultSmallThumbnail,
    divisions: Set<String>? = defaultDivisions,
    authors: Set<String>? = defaultAuthors,
    keywords: Set<String>? = defaultKeywords,
    description: String? = defaultDescription,
    price: Double? = defaultPrice,
    original: Map<OriginalPropertyKey, String>? = defaultOriginal,
    isNew: Boolean = true
): BookDetails {
  val book = BookDetails(createBookContext(isbn, title, publisher, publishDate, seriesCode, seriesIsbn, largeThumbnail, mediumThumbnail, smallThumbnail, divisions, authors, keywords, description, price, original))
    if (!isNew) {
        book.markingPersistedEntity()
    }
    return book
}

fun createBookFilterOperator(
    idGenerator: BookDetailsFilterIdGenerator = defaultBookOriginalFilterIdGenerator,
    operatorType: Operator.OperatorType,
    children: List<BookOriginalFilter>,
    mappingType: MappingType = defaultBookOriginalMappingType
): BookOriginalFilter {
    val filter = BookOriginalFilter(idGenerator, mappingType)
    filter.root = true
    filter.propertyRegex = null
    filter.operatorType = operatorType
    filter.children = children.toMutableList()
    return filter
}

fun createBookFilterOperand(
    idGenerator: BookDetailsFilterIdGenerator = defaultBookOriginalFilterIdGenerator,
    propertyRegex: PropertyRegex,
    mappingType: MappingType = defaultBookOriginalMappingType
): BookOriginalFilter {
    val filter = BookOriginalFilter(idGenerator, mappingType)
    filter.root = false
    filter.operatorType = null
    filter.children = null
    filter.mappingType = mappingType
    filter.propertyRegex = propertyRegex
    return filter
}