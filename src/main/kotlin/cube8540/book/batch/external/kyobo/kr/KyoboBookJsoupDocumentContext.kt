package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.domain.*
import org.jsoup.nodes.Document
import java.net.URI
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

class KyoboBookJsoupDocumentContext(private val document: Document, private val divisionMapper: DivisionRawMapper): BookDetailsContext {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())

        private const val meta = "meta"
        private const val input = "input"
        private const val name = "name"
        private const val property = "property"
        private const val content = "content"
        private const val value = "value"

        private val mappingType = MappingType.KYOBO
    }

    private val metaTags = document.getElementsByTag(meta)
    private val inputTags = document.getElementsByTag(input)

    override fun resolveIsbn(): String = metaTags
        .first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.isbn) }
        .attr(content)

    override fun resolveTitle(): String? = metaTags
        .first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.title) }
        .attr(content)

    override fun resolveSeriesCode(): String? {
        val seriesCode = inputTags.first { it.attr(name).equals(KyoboBookInputNameSelector.seriesBarcode) }?.attr(value)
        val aBarcode = inputTags.first { it.attr(name).equals(KyoboBookInputNameSelector.aBarcode) }.attr(value)
        return if (seriesCode != null && seriesCode.isNotEmpty()) {
            seriesCode
        } else if (aBarcode.isNotEmpty()) {
            aBarcode
        } else {
            null
        }
    }

    override fun resolveSeriesIsbn(): String? = null

    override fun resolveDivisions(): Set<String> {
        val rawDivisions = inputTags.first { it.attr(name).equals(KyoboBookInputNameSelector.categoryCode) }.attr(value)
        return divisionMapper.mapping(convertCategoryCodeToRawDivisions(rawDivisions)).toSet()
    }

    override fun resolvePublisher(): String? = null

    override fun resolvePublishDate(): LocalDate? = null

    override fun resolveAuthors(): Set<String> {
        val authors = metaTags.first { it.attr(name).equals(KyoboBookMetaTagNameSelector.author) }.attr(content)
        return authors.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    override fun resolveThumbnail(): Thumbnail {
        val largeThumbnail = metaTags
            .first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.largeThumbnail) }
            .attr(content)
        val mediumThumbnail = metaTags
            .first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.mediumThumbnail) }
            .attr(content)

        return Thumbnail(
            largeThumbnail = URI.create(largeThumbnail),
            mediumThumbnail = URI.create(mediumThumbnail),
            smallThumbnail = null
        )
    }

    override fun resolveDescription(): String? = document.select(KyoboBookClassSelector.description)?.first()?.text()

    override fun resolveKeywords(): Set<String>? = null

    override fun resolvePrice(): Double? = metaTags
        .first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.originalPrice) }
        .attr(content)?.toDouble()

    override fun resolveOriginal(): Map<OriginalPropertyKey, String?> {
        val original = HashMap<OriginalPropertyKey, String?>()
        original[OriginalPropertyKey(KyoboBookMetaTagNameSelector.author, mappingType)] =
            metaTags.first { it.attr(name).equals(KyoboBookMetaTagNameSelector.author) }.attr(content)
        original[OriginalPropertyKey(KyoboBookMetaTagPropertySelector.title, mappingType)] = resolveTitle()
        original[OriginalPropertyKey(KyoboBookMetaTagPropertySelector.largeThumbnail, mappingType)] = metaTags
            .first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.largeThumbnail) }
            .attr(content)
        original[OriginalPropertyKey(KyoboBookMetaTagPropertySelector.mediumThumbnail, mappingType)] = metaTags
            .first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.mediumThumbnail) }
            .attr(content)
        original[OriginalPropertyKey(KyoboBookMetaTagPropertySelector.originalPrice, mappingType)] = metaTags
            .first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.originalPrice) }
            .attr(content)
        original[OriginalPropertyKey(KyoboBookInputNameSelector.seriesBarcode, mappingType)] =
            inputTags.first { it.attr(name).equals(KyoboBookInputNameSelector.seriesBarcode) }?.attr(value)
        original[OriginalPropertyKey(KyoboBookInputNameSelector.aBarcode, mappingType)] =
            inputTags.first { it.attr(name).equals(KyoboBookInputNameSelector.aBarcode) }.attr(value)
        original[OriginalPropertyKey(KyoboBookInputNameSelector.categoryCode, mappingType)] = inputTags
            .first { it.attr(name).equals(KyoboBookInputNameSelector.categoryCode) }.attr(value)
        return original
    }

    override fun createdAt(): LocalDateTime = LocalDateTime.now(clock)

    override fun equals(other: Any?): Boolean = when (other) {
        null -> {
            false
        }
        is KyoboBookJsoupDocumentContext -> {
            other.document == this.document
        }
        else -> {
            false
        }
    }

    override fun hashCode(): Int = document.hashCode()

    private fun convertCategoryCodeToRawDivisions(text: String): List<String> {
        val textGroup = ArrayList<String>()
        text.forEachIndexed { index, c ->
            val textIndex = index / 2
            when (val t = textGroup.getOrNull(textIndex)) {
                null -> textGroup.add(c.toString())
                else -> textGroup[textIndex] = t + c.toString()
            }
        }
        return textGroup.mapIndexed { index, _ -> textGroup.subList(0, (index + 1)).joinToString("") }
    }
}