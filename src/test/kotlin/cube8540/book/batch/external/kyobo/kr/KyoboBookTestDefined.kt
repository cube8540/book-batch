package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.book.domain.*
import cube8540.book.batch.getQueryParams
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.jsoup.nodes.Document
import java.net.URI
import java.time.LocalDateTime

const val defaultHeaderCookieName = "Set-Cookie"
const val defaultHeaderCookieKey = "defaultCookieKey"
const val defaultHeaderCookieValue = "defaultCookieValue"

const val documentURL = "https://localhost-documents"

const val defaultOriginalBarcode = "og:$defaultIsbn"
const val defaultABarcode = "aBarcode9791133447831"
const val defaultCategoryCode = "010101"
const val defaultDocumentAuthors = ""

const val defaultKyoboLoginUsername = "defaultUsername00000"
const val defaultKyoboLoginPassword = "defaultPassword00000"

val defaultKyoboLoginIssuedAt: LocalDateTime = LocalDateTime.of(2021, 5, 5, 20, 35, 0)
val defaultKyoboLoginExpiredDateTime: LocalDateTime = defaultKyoboLoginIssuedAt.plusSeconds(KyoboLoginFilter.expiredSeconds.toLong()).plusNanos(1)
val defaultKyoboLoginNotExpiredDateTime: LocalDateTime = defaultKyoboLoginIssuedAt.plusSeconds(KyoboLoginFilter.expiredSeconds.toLong()).minusNanos(1)

fun createDocument(
    url: String? = documentURL,
    isbn: String? = defaultIsbn,
    originalBarcode: String? = defaultOriginalBarcode,
    authors: String? = defaultDocumentAuthors,
    title: String? = defaultTitle,
    largeThumbnail: String? = defaultLargeThumbnail.toString(),
    mediumThumbnail: String? = defaultMediumThumbnail.toString(),
    price: Double? = defaultPrice,
    seriesBarcode: String? = defaultSeriesCode,
    aBarcode: String? = defaultABarcode,
    description: String? = defaultDescription,
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

    price?.let {
        document.appendElement("meta")
            .attr("property", KyoboBookMetaTagPropertySelector.originalPrice)
            .attr("content", price.toString())
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

    description?.let {
        document.appendElement("div").addClass("content_middle")
            .appendElement("div").addClass("content_left")
                .appendElement("div").addClass("box_detail_content")
                    .appendElement("div").addClass("box_detail_article")
                    .text(description)
    }

    return document
}

fun createKyoboLoginInfo(
    cookies: Map<String, String> = emptyMap(),
    issuedAt: LocalDateTime = defaultKyoboLoginIssuedAt
): KyoboLoginInfo = KyoboLoginInfo(cookies, issuedAt)

fun createHeaderCookieValue(
    key: String = defaultHeaderCookieKey,
    value: String = defaultHeaderCookieValue
) = "${key}=${value}"

fun createKyoboLoginDispatcher(
    url: String = KyoboBookRequestNames.loginUrl,
    username: String = defaultKyoboLoginUsername,
    password: String = defaultKyoboLoginPassword,
    result: MockResponse
): Dispatcher {
    return object: Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val queryParameter = request.path?.let { URI.create(it).getQueryParams() } ?: emptyMap()
            val expectedQueryParams = mapOf(
                KyoboBookRequestNames.username to listOf(username),
                KyoboBookRequestNames.password to listOf(password)
            )
            return if (request.requestUrl?.toUri()?.path == url && queryParameter == expectedQueryParams) {
                return result
            } else {
                MockResponse().setResponseCode(404)
            }
        }
    }
}

fun createKyoboBookRequestDispatcher(
    url: String = KyoboBookRequestNames.kyoboBookDetailsPath,
    isbn: String = defaultIsbn,
    result: MockResponse
): Dispatcher {
    return object: Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val queryParams = request.path?.let { URI.create(it).getQueryParams() } ?: emptyMap()
            val expectedQueryParams = mapOf(
                KyoboBookRequestNames.isbn to listOf(isbn)
            )
            return if (request.requestUrl?.toUri()?.path == url && queryParams == expectedQueryParams) {
                result
            } else {
                MockResponse().setResponseCode(404)
            }
        }
    }
}