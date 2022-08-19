package cube8540.book.batch.translator.nl.go.client

import feign.Param
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.web.bind.annotation.GetMapping
import java.beans.ConstructorProperties
import java.time.LocalDate

const val MAX_REQUEST_PAGE = 999
const val ERROR_RESULT = "ERROR"

data class NationalLibraryBookRequest(
    @get:Param("cert_key")
    val secretKey: String,

    @get:Param("result_style")
    val resultStyle: String = DEFAULT_RESULT_STYLE,

    @get:Param("publisher")
    val publisher: String? = null,

    @get:Param("isbn")
    val isbn: String? = null,

    @get:Param("page_no")
    val pageNo: Int,

    @get:Param("page_size")
    val pageSize: Int,

    @get:Param("start_publish_date")
    val startPublishDate: String? = null,

    @get:Param("end_publish_date")
    val endPublishDate: String? = null,

    @get:Param("ebook_yn")
    val ebookYn: String? = DEFAULT_EBOOK_YN
) {
    companion object {
        const val DEFAULT_RESULT_STYLE = "json"
        const val DEFAULT_EBOOK_YN = "N"
    }
}

data class NationalLibraryClientResponse
@ConstructorProperties(value = [TOTAL_COUNT, PAGE_NO, DOCUMENTS, RESULT, ERROR_CODE, ERROR_MESSAGE])
constructor(
    val totalCount: Int = 0,
    val pageNo: Int = 0,
    val docs: List<Book>? = null,

    // 에러 관련
    val result: String? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null
) {
    companion object {
        const val RESULT = "RESULT"
        const val ERROR_CODE = "ERR_CODE"
        const val ERROR_MESSAGE = "ERR_MESSAGE"
        const val PAGE_NO = "PAGE_NO"
        const val TOTAL_COUNT = "TOTAL_COUNT"
        const val DOCUMENTS = "docs"
        const val TITLE = "TITLE"
        const val ISBN = "EA_ISBN"
        const val SET_ISBN = "SET_ISBN"
        const val EA_ADD_CODE = "EA_ADD_CODE"
        const val SET_ADD_CODE = "SET_ADD_CODE"
        const val SERIES_NO = "SERIES_NO"
        const val SET_EXPRESSION = "SET_EXPRESSION"
        const val SUBJECT = "SUBJECT"
        const val PUBLISHER = "PUBLISHER"
        const val AUTHOR = "AUTHOR"
        const val REAL_PUBLISH_DATE = "REAL_PUBLISH_DATE"
        const val PUBLISH_PREDATE = "PUBLISH_PREDATE"
        const val UPDATE_DATE = "UPDATE_DATE"
    }

    data class Book
    @ConstructorProperties(value = [TITLE, ISBN, SET_ISBN, EA_ADD_CODE, SET_ADD_CODE, SERIES_NO, SET_EXPRESSION, SUBJECT, PUBLISHER, AUTHOR, REAL_PUBLISH_DATE, PUBLISH_PREDATE, UPDATE_DATE])
    constructor(
        val title: String,
        val isbn: String? = null,
        val setIsbn: String? = null,
        val additionalCode: String? = null,
        val setAdditionalCode: String? = null,
        val seriesNo: Int? = null,
        val setExpression: String? = null,
        val subject: String? = null,
        val publisher: String,
        val author: String? = null,
        val realPublishDate: LocalDate? = null,
        val publishPreDate: LocalDate? = null,
        val updateDate: LocalDate? = null
    )
}

data class NationalLibraryAPIKey(val key: String)

@FeignClient(name = "nationalLibraryClient", url =  "https://seoji.nl.go.kr", configuration = [NationalLibraryClientConfiguration::class])
interface NationalLibraryClient {

    @GetMapping("/landingPage/SearchApi.do")
    fun search(@SpringQueryMap request: NationalLibraryBookRequest): NationalLibraryClientResponse
}