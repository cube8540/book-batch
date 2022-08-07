package cube8540.book.batch.translator.kyobo.kr

import cube8540.book.batch.book.domain.*
import cube8540.book.batch.translator.kyobo.kr.client.KyoboBookClassSelector
import cube8540.book.batch.translator.kyobo.kr.client.KyoboBookInputNameSelector
import cube8540.book.batch.translator.kyobo.kr.client.KyoboBookMetaTagNameSelector
import cube8540.book.batch.translator.kyobo.kr.client.KyoboBookMetaTagPropertySelector
import org.jsoup.nodes.Document
import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val documentURL = "https://localhost-documents"

const val defaultOriginalBarcode = "og:$defaultIsbn"
const val defaultABarcode = "aBarcode9791133447831"
const val defaultCategoryCode = "010101"
const val defaultDocumentAuthors = ""

val defaultPublishDate: LocalDate = LocalDate.of(2021, 11, 14)
val defaultPublishDateHtml: String = defaultPublishDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + " 출간"

val defaultIndexes = listOf("index0001", "index0002", "index0003")
val defaultIndexesHtml = defaultIndexes.joinToString(separator = "<br >")

fun createDocument(
    url: String? = documentURL,
    isbn: String? = defaultIsbn,
    originalBarcode: String? = defaultOriginalBarcode,
    authors: String? = defaultDocumentAuthors,
    title: String? = defaultTitle,
    publishDate: String? = defaultPublishDateHtml,
    largeThumbnail: String? = defaultLargeThumbnail.toString(),
    mediumThumbnail: String? = defaultMediumThumbnail.toString(),
    originalPrice: Double? = defaultOriginalPrice,
    salePrice: Double? = defaultSalePrice,
    seriesBarcode: String? = defaultSeriesCode,
    aBarcode: String? = defaultABarcode,
    description: String? = defaultDescription,
    indexHtml: String? = defaultIndexesHtml,
    categoryCode: String? = defaultCategoryCode
): Document {
    val document = Document(url)

    isbn?.let {
        document.appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.isbn)
            .attr("content", isbn)
    }

    originalBarcode?.let {
        document.appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.originalBarcode)
            .attr("content", originalBarcode)
    }

    authors?.let {
        document.appendElement("meta")
            .attr("name", KyoboBookMetaTagNameSelector.author)
            .attr("content", authors)
    }

    title?.let {
        document.appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.title)
            .attr("content", title)
    }

    publishDate?.let {
        document.appendElement("span")
            .addClass(KyoboBookClassSelector.publishDate.replace(".", ""))
            .appendText(it)
    }

    largeThumbnail?.let {
        document.appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.largeThumbnail)
            .attr("content", largeThumbnail)
    }

    mediumThumbnail?.let {
        document.appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.mediumThumbnail)
            .attr("content", mediumThumbnail)
    }

    originalPrice?.let {
        document.appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.originalPrice)
            .attr("content", originalPrice.toString())
    }

    salePrice?.let {
        document.appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.salePrice)
            .attr("content", salePrice.toString())
    }

    seriesBarcode?.let {
        document.appendElement("input")
            .attr("name", KyoboBookInputNameSelector.seriesBarcode)
            .attr("value", seriesBarcode)
    }

    aBarcode?.let {
        document.appendElement("input")
            .attr("name", KyoboBookInputNameSelector.aBarcode)
            .attr("value", aBarcode)
    }

    categoryCode?.let {
        document.appendElement("input")
            .attr("name", KyoboBookInputNameSelector.categoryCode)
            .attr("value", categoryCode)
    }

    val contentAttribute = document.appendElement("div").addClass("content_middle")
        .appendElement("div").addClass("content_left")
        .appendElement("div").addClass("box_detail_content")

    description?.let {
        contentAttribute.append("<!-- *** s:책소개 *** -->")
        contentAttribute.append("<!-- 2021-11-01 -->")
        contentAttribute.appendElement("div").addClass("box_detail_article").append(description)

        contentAttribute.appendElement("dev")
    }

    indexHtml?.let {
        contentAttribute.append("<!-- *** s:목차 *** -->")
        contentAttribute.append("<!-- 2021-11-01 -->")
        contentAttribute.appendElement("h2").addClass("title_detail_basic").addClass("목차")
        contentAttribute.appendElement("dev").addClass("box_detail_article").append(indexHtml)
    }

    // 출판사 총평과 같은 기타 DEV 태그
    contentAttribute.appendElement("dev").addClass("box_detail_article").append("")

    return document
}