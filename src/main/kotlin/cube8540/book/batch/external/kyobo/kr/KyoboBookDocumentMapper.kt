package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.DivisionRawMapper
import cube8540.book.batch.external.BookDocumentMapper
import org.jsoup.nodes.Document
import java.net.URI

class KyoboBookDocumentMapper(private val divisionRawMapper: DivisionRawMapper): BookDocumentMapper {

    companion object {
        const val meta = "meta"
        const val input = "input"
        const val name = "name"
        const val property = "property"
        const val content = "content"
        const val value = "value"
    }

    override fun convertValue(document: Document): BookDetails {
        val metaTags = document.getElementsByTag(meta)
        val inputTags = document.getElementsByTag(input)

        val isbn = metaTags.first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.isbn) }.attr(content)

        val bookDetails = BookDetails(isbn)
        bookDetails.authors = metaTags.first { it.attr(name).equals(KyoboBookMetaTagNameSelector.author) }.attr(content).split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        bookDetails.title = metaTags.first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.title) }.attr(content)
        bookDetails.largeThumbnail = URI.create(metaTags.first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.largeThumbnail) }.attr(content))
        bookDetails.mediumThumbnail = URI.create(metaTags.first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.mediumThumbnail) }.attr(content))
        bookDetails.price = metaTags.first { it.attr(property).equals(KyoboBookMetaTagPropertySelector.originalPrice) }.attr(content).toDouble()

        bookDetails.seriesCode = inputTags.first { it.attr(name).equals(KyoboBookInputNameSelector.seriesBarcode) }.attr(value)

        val rawDivisions = inputTags.first { it.attr(name).equals(KyoboBookInputNameSelector.categoryCode) }.attr(value)
        bookDetails.divisions = divisionRawMapper.mapping(convertCategoryCodeToRawDivisions(rawDivisions)).toSet()

        bookDetails.description = document.select(KyoboBookClassSelector.description).first().text()

        return bookDetails
    }

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