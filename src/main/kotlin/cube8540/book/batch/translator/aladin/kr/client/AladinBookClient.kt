package cube8540.book.batch.translator.aladin.kr.client

import feign.Param
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.web.bind.annotation.GetMapping
import java.beans.ConstructorProperties
import java.time.LocalDate

const val MAX_REQUEST_DATA_COUNT = 200
const val MAX_RESULTS = 50

data class AladinBookRequest(
    @get:Param("ttbkey")
    val ttbKey: String,

    @get:Param("QueryType")
    val queryType: String = DEFAULT_QUERY_TYPE,

    @get:Param("Query")
    val query: String,

    val start: Int,

    @get:Param("MaxResults")
    val maxResults: Int = DEFAULT_MAX_RESULTS,

    @get:Param("SearchTarget")
    val searchTarget: String = DEFAULT_SEARCH_TARGET,

    val output: String = DEFAULT_OUTPUT,

    @get:Param("Sort")
    val sort: String = DEFAULT_SORT,

    @get:Param("Version")
    val version: String = DEFAULT_VERSION
) {
    companion object {
        const val DEFAULT_QUERY_TYPE = "Publisher"
        const val DEFAULT_SEARCH_TARGET = "Book"
        const val DEFAULT_OUTPUT = "js"
        const val DEFAULT_VERSION = "20131101"
        const val DEFAULT_SORT = "PublishTime"
        const val DEFAULT_MAX_RESULTS = MAX_RESULTS
    }
}

data class AladinBookClientResponse
@ConstructorProperties(value = [TOTAL_RESULTS, START_INDEX, ITEMS_PER_PAGE, ITEMS])
constructor(
    val totalResults: Int = 0,
    val startIndex: Int = 1,
    val itemsPerPage: Int = 0,
    val items: List<Book>? = null
) {

    companion object {
        const val TOTAL_RESULTS = "totalResults"
        const val START_INDEX = "startIndex"
        const val ITEMS_PER_PAGE = "itemsPerPage"

        const val ITEMS = "item"
        const val TITLE = "title"
        const val AUTHOR = "author"
        const val PUBLISHER = "publisher"
        const val PUBLISH_DATE = "pubDate"
        const val ISBN = "isbn"
        const val ISBN_13 = "isbn13"
        const val PRICE_STANDARD = "priceStandard"
        const val PRICE_SALES = "priceSales"
        const val CATEGORY_ID = "categoryId"
        const val LINK = "link"
    }

    data class Book
    @ConstructorProperties(value = [TITLE, LINK, AUTHOR, PUBLISHER, PUBLISH_DATE, ISBN, ISBN_13, CATEGORY_ID, PRICE_STANDARD, PRICE_SALES])
    constructor(
        val title: String? = null,
        val link: String? = null,
        val author: String? = null,
        val publisher: String? = null,
        val publishDate: LocalDate? = null,
        val isbn: String? = null,
        val isbn13: String,
        val categoryId: Int? = null,
        val priceStandard: Int? = null,
        val priceSales: Int? = null
    )
}

data class AladinAuthenticationInfo(val ttbKey: String)

@FeignClient(name = "aladinBookClient", url = "https://www.aladin.co.kr", configuration = [AladinBookClientConfiguration::class])
interface AladinBookClient {

    @GetMapping("/ttb/api/ItemSearch.aspx")
    fun search(@SpringQueryMap request: AladinBookRequest): AladinBookClientResponse
}