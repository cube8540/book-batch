package cube8540.book.batch.translator.aladin.kr.client

import cube8540.book.batch.book.domain.*
import java.time.LocalDate
import kotlin.random.Random

const val defaultTtbKey = "ttbKey000000"
const val defaultIsbn10 = "113344783X"

val defaultCategoryId = Random.nextInt(0, Int.MAX_VALUE)

fun createBook(
    isbn: String = defaultIsbn,
    isbn10: String? = defaultIsbn10,
    title: String? = defaultTitle,
    link: String? = defaultLink,
    author: String? = defaultAuthors.joinToString(", "),
    categoryId: Int? = defaultCategoryId,
    originalPrice: Double? = defaultOriginalPrice,
    salePrice: Double? = defaultSalePrice,
    publisher: String? = defaultPublisher,
    publishDate: LocalDate? = defaultPublishDate
): AladinBookClientResponse.Book = AladinBookClientResponse.Book(
    isbn = isbn10,
    isbn13 = isbn,
    title = title,
    link = link,
    author = author,
    categoryId = categoryId,
    priceStandard = originalPrice?.toInt(),
    priceSales = salePrice?.toInt(),
    publisher = publisher,
    publishDate =  publishDate
)