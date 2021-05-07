package cube8540.book.batch.external

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.OriginalPropertyKey
import okhttp3.mockwebserver.MockResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

object WebClientBookReaderTestEnvironment {

    internal const val endpoint = "/endpoint"
    internal val kotlinObjectMapper = ObjectMapper()
        .registerModule(KotlinModule())
        .registerModule(
            SimpleModule().addKeyDeserializer(OriginalPropertyKey::class.java, object: KeyDeserializer() {
                override fun deserializeKey(key: String?, ctxt: DeserializationContext?): Any = "key"
            })
        )

    internal const val errorCode = "errorCode0001"
    internal const val errorMessage = "errorMessage0001"

    internal const val pageRequestName = "PageRequestName"
    internal const val pageSizeRequestName = "PageSizeRequestName"

    internal const val totalCount = 1L
    internal const val pageNumber = 1L
    internal const val isbn = "9791136202093"

    internal val bookDetails = BookDetails(isbn)

    internal val mockResponse = MockResponse()
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody(kotlinObjectMapper.writeValueAsString(BookAPIResponse(totalCount, pageNumber, listOf(bookDetails))))
    internal val mockErrorResponse = MockResponse()
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setResponseCode(400)
        .setBody(kotlinObjectMapper.writeValueAsString(BookAPIErrorResponse(errorCode, errorMessage)))

}