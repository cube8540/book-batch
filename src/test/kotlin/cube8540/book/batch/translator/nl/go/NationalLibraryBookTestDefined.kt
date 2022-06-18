package cube8540.book.batch.translator.nl.go

import cube8540.book.batch.book.domain.*
import cube8540.book.batch.translator.nl.go.client.ERROR_RESULT
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClientResponse
import java.time.LocalDate
import java.util.*

const val defaultAdditionalCode = "07650"

val defaultRealPublishDate: LocalDate = LocalDate.of(2021, 6, 6)

const val defaultExpression = ""

const val defaultErrorMessage = "인증키 정보가 없습니다."
const val defaultErrorCode = "010"
const val defaultErrorResult = ERROR_RESULT

val defaultNationalLibraryClientKey = UUID.randomUUID().toString()

fun createBook(
    title: String = defaultTitle,
    isbn: String? = defaultIsbn,
    setIsbn: String? = defaultSeriesIsbn,
    additionalCode: String? = defaultAdditionalCode,
    seriesNo: Int? = null,
    setExpression: String? = defaultExpression,
    subject: String? = null,
    publisher: String = defaultPublisher,
    author: String? = null,
    realPublishDate: LocalDate = defaultRealPublishDate,
    publishPreDate: LocalDate? = defaultPublishDate,
    updateDate: LocalDate? = null
): NationalLibraryClientResponse.Book = NationalLibraryClientResponse.Book(
    title = title,
    isbn = isbn,
    setIsbn = setIsbn,
    additionalCode = additionalCode,
    setAdditionalCode = null,
    seriesNo = seriesNo,
    setExpression = setExpression,
    subject = subject,
    publisher = publisher,
    author = author,
    realPublishDate = realPublishDate,
    publishPreDate = publishPreDate,
    updateDate = updateDate
)