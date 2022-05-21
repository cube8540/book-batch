package cube8540.book.batch.interlock.naver.com.client

import cube8540.book.batch.book.domain.*
import java.time.LocalDate
import java.util.*

const val defaultNaverBookAPIResponseIsbn = "1136215301 $defaultIsbn"

const val defaultErrorMessage = "인증키 정보가 없습니다."
const val defaultErrorCode = "024"

val defaultClientId = UUID.randomUUID().toString()
val defaultClientSecret = UUID.randomUUID().toString()

fun createErrorString(
    errorMessage: String = defaultErrorMessage,
    errorCode: String = defaultErrorCode
) = "{\"errorMessage\": \"$errorMessage\", \"errorCode\": \"$errorCode\"}"

fun createBook(
    isbn: String? = defaultNaverBookAPIResponseIsbn,
    title: String? = defaultTitle,
    image: String? = defaultSmallThumbnail.toString(),
    publisher: String? = defaultPublisherCode,
    publishDate: LocalDate? = defaultPublishDate
): NaverBookClientResponse.Book = NaverBookClientResponse.Book(
    title = title,
    image = image,
    publisher = publisher,
    publishDate = publishDate,
    isbn = isbn
)