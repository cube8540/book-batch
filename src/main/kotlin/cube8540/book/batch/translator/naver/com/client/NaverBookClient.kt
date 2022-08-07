package cube8540.book.batch.translator.naver.com.client

import feign.Param
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.web.bind.annotation.GetMapping
import java.beans.ConstructorProperties
import java.time.LocalDate

data class NaverBookClientError
@ConstructorProperties(value = [errorMessage, errorCode])
constructor(var errorMessage: String, var errorCode: String) {
    companion object {
        const val errorCode = "errorCode"
        const val errorMessage = "errorMessage"
    }
}

data class NaverBookClientRequest(
    val start: Int = 1,

    val display: Int = 10,

    @get:Param("d_publ")
    val publisher: String? = null,

    @get:Param("d_dafr")
    val from: String? = null,

    @get:Param("d_dato")
    val to: String? = null,

    @get:Param("d_isbn")
    val isbn: String? = null
)

data class NaverBookClientResponse
@ConstructorProperties(value = [totalCount, start, display, item])
constructor(
    val total: Int = 0,
    val start: Int = 1,
    val display: Int = 10,
    val items: List<Book>? = null
) {
    companion object {
        const val totalCount = "total"
        const val start = "start"
        const val display = "display"
        const val item = "items"

        const val isbn = "isbn"
        const val title = "title"
        const val link = "link"
        const val image = "image"
        const val author = "author"
        const val price = "price"
        const val discount = "discount"
        const val publisher = "publisher"
        const val publishDate = "pubdate"
        const val description = "description"
    }

    data class Book
    @ConstructorProperties(value = [title, link, image, author, price, display, publisher, publishDate, isbn, description])
    constructor(
        val title: String? = null,
        val link: String? = null,
        val image: String? = null,
        val author: String? = null,
        val price: Int? = null,
        val discount: Int? = null,
        val publisher: String? = null,
        val publishDate: LocalDate? = null,
        val isbn: String? = null,
        val description: String? = null
    )
}

@FeignClient(name = "naverBookClient", url = "https://openapi.naver.com", configuration = [NaverBookClientConfiguration::class])
interface NaverBookClient {

    @GetMapping("/v1/search/book_adv.json")
    fun search(@SpringQueryMap request: NaverBookClientRequest): NaverBookClientResponse

}