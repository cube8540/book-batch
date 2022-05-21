package cube8540.book.batch.translator.naver.com.client

import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.author
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.description
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.display
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.image
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.isbn
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.item
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.link
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.price
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.publishDate
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.publisher
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.start
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.title
import cube8540.book.batch.translator.naver.com.client.NaverBookClientResponseNames.totalCount
import java.beans.ConstructorProperties
import java.time.LocalDate

data class NaverBookClientResponse
@ConstructorProperties(value = [totalCount, start, display, item])
constructor(
    val total: Int = 0,
    val start: Int = 1,
    val display: Int = 10,
    val items: List<Book>? = null
) {
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