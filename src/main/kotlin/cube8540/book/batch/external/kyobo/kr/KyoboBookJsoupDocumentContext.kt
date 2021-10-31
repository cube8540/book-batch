package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.book.domain.*
import org.jsoup.nodes.Comment
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

class KyoboBookJsoupDocumentContext(private val document: Document, private val divisionMapper: DivisionRawMapper):
    BookDetailsContext {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())

        private const val meta = "meta"
        private const val input = "input"
        private const val name = "name"
        private const val property = "property"
        private const val content = "content"
        private const val value = "value"

        private val mappingType = MappingType.KYOBO

        private val boxDescriptionCommentRegex = Regex("^(\\*{3})(\\S|\\s)+(\\*{3})\$")
    }

    private val metaTags = document.getElementsByTag(meta)
    private val inputTags = document.getElementsByTag(input)

    override fun resolveIsbn(): String = metaTags
        .first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.isbn) }.attr(content)

    override fun resolveTitle(): String? = metaTags
        .find { it.attr(property).equals(KyoboBookMetaTagPropertySelector.title) }?.attr(content)

    override fun resolveSeriesCode(): String? {
        val seriesCode = inputTags.find { it.attr(name).equals(KyoboBookInputNameSelector.seriesBarcode) }?.attr(value)
        val aBarcode = inputTags.find { it.attr(name).equals(KyoboBookInputNameSelector.aBarcode) }?.attr(value)
        return if (seriesCode != null && seriesCode.isNotEmpty()) {
            seriesCode
        } else if (aBarcode?.isNotEmpty() == true) {
            aBarcode
        } else {
            null
        }
    }

    override fun resolveSeriesIsbn(): String? = null

    override fun resolveDivisions(): Set<String> {
        return inputTags.find { it.attr(name).equals(KyoboBookInputNameSelector.categoryCode) }
            ?.attr(value)
            ?.let { divisionMapper.mapping(convertCategoryCodeToRawDivisions(it)).toSet() }
            ?: emptySet()
    }

    override fun resolvePublisher(): String? = null

    override fun resolvePublishDate(): LocalDate? = null

    override fun resolveAuthors(): Set<String> {
        return metaTags.find { it.attr(name).equals(KyoboBookMetaTagNameSelector.author) }
                ?.attr(content)?.split(",")?.map { v -> v.trim() }?.filter { v-> v.isNotEmpty() }?.toSet() ?: emptySet()
    }

    override fun resolveThumbnail(): Thumbnail {
        val largeThumbnail = metaTags
            .find { it.attr(property).equals(KyoboBookMetaTagPropertySelector.largeThumbnail) }
            ?.attr(content)
        val mediumThumbnail = metaTags
            .find { it.attr(property).equals(KyoboBookMetaTagPropertySelector.mediumThumbnail) }
            ?.attr(content)

        return Thumbnail(
            largeThumbnail = largeThumbnail?.let { URI.create(it) },
            mediumThumbnail = mediumThumbnail?.let { URI.create(it) },
            smallThumbnail = null
        )
    }

    override fun resolveDescription(): String? {
        val element = findArticleByCommentText(KyoboBookCommentText.descriptionCommentText)
        element?.select("br")?.append("\\n")
        element?.select("p")?.prepend("\\n\\n")

        return element?.text()
    }

    override fun resolveIndex(): List<String>? {
        val element = findArticleByCommentText(KyoboBookCommentText.indexCommentText)
        val indexList = element?.html()
            ?.replace("\r", "")
            ?.replace("\n", "")
            ?.replace("\t", "")
            ?.replace(Regex("\\s+"), " ")
            ?.split("<br>", ignoreCase = true)
        return indexList?.filter { it.isNotEmpty() && it.isNotBlank() }?.map { it.trim() }
    }

    override fun resolveKeywords(): Set<String>? = null

    override fun resolveOriginal(): Map<OriginalPropertyKey, String?> {
        val original = HashMap<OriginalPropertyKey, String?>()
        original[OriginalPropertyKey(KyoboBookMetaTagNameSelector.author, mappingType)] =
            metaTags.find { it.attr(name).equals(KyoboBookMetaTagNameSelector.author) }?.attr(content)
        original[OriginalPropertyKey(KyoboBookMetaTagPropertySelector.title, mappingType)] = resolveTitle()
        original[OriginalPropertyKey(KyoboBookMetaTagPropertySelector.largeThumbnail, mappingType)] = metaTags
            .find { it.attr(property).equals(KyoboBookMetaTagPropertySelector.largeThumbnail) }
            ?.attr(content)
        original[OriginalPropertyKey(KyoboBookMetaTagPropertySelector.mediumThumbnail, mappingType)] = metaTags
            .find { it.attr(property).equals(KyoboBookMetaTagPropertySelector.mediumThumbnail) }
            ?.attr(content)
        original[OriginalPropertyKey(KyoboBookMetaTagPropertySelector.originalPrice, mappingType)] = metaTags
            .find { it.attr(property).equals(KyoboBookMetaTagPropertySelector.originalPrice) }
            ?.attr(content)
        original[OriginalPropertyKey(KyoboBookInputNameSelector.seriesBarcode, mappingType)] =
            inputTags.find { it.attr(name).equals(KyoboBookInputNameSelector.seriesBarcode) }?.attr(value)
        original[OriginalPropertyKey(KyoboBookInputNameSelector.aBarcode, mappingType)] =
            inputTags.find { it.attr(name).equals(KyoboBookInputNameSelector.aBarcode) }?.attr(value)
        original[OriginalPropertyKey(KyoboBookInputNameSelector.categoryCode, mappingType)] = inputTags
            .find { it.attr(name).equals(KyoboBookInputNameSelector.categoryCode) }?.attr(value)
        return original
    }

    override fun resolveExternalLink(): Map<MappingType, BookExternalLink> {
        val uriComponentBuilder = UriComponentsBuilder.newInstance()
            .uri(URI.create(KyoboBookRequestNames.kyoboDomain + KyoboBookRequestNames.kyoboBookDetailsPath))
            .queryParam(KyoboBookRequestNames.isbn, resolveIsbn())
            .build()

        val originalPrice = metaTags.find { it.attr(property) == KyoboBookMetaTagPropertySelector.originalPrice }
            ?.attr(content)
            ?.toDouble()
        val salePrice = metaTags.find { it.attr(property) == KyoboBookMetaTagPropertySelector.salePrice }
            ?.attr(content)
            ?.toDouble()

        return mapOf(MappingType.KYOBO to BookExternalLink(uriComponentBuilder.toUri(), originalPrice, salePrice))
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

    private fun findArticleByCommentText(commentText: String): Element? {
        val elements = document.select(KyoboBookClassSelector.bookContent)
        return elements.find { findFirstComment(it)?.data?.replace(" ", "") == commentText }
    }

    private fun findFirstComment(node: Node?): Comment? {
        if (node == null) {
            return null
        }
        if (node !is Comment) {
            return findFirstComment(node.previousSibling())
        }
        return if (isBoxDescriptionComment(node)) {
            node
        } else {
            findFirstComment(node.previousSibling())
        }
    }

    private fun isBoxDescriptionComment(comment: Comment): Boolean =
        comment.data?.let { boxDescriptionCommentRegex.matches(it.trim()) } ?: false
}