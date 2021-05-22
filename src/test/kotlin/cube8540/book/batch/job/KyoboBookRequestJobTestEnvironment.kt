package cube8540.book.batch.job

import cube8540.book.batch.config.AuthenticationProperty
import cube8540.book.batch.getQueryParams
import cube8540.book.batch.infra.kyobo.kr.KyoboBookRequestNames
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.jsoup.Jsoup
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.URI
import java.nio.file.Files
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object KyoboBookRequestJobTestEnvironment {

    private const val exampleDetailsPageFile = "job/kyobo-book-details-page.html"

    internal val from = LocalDate.of(2021, 1, 1)
    internal val to = LocalDate.of(2021, 5, 31)

    private const val loginCookieKey = "loginCookie"
    private const val loginCookieValue = "loginCookieValue"
    private const val setHeaderCookie = "Set-Cookie"

    internal val loginSuccessfulResponse = MockResponse().setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML)
        .setHeader(setHeaderCookie, "$loginCookieKey=$loginCookieValue")
    internal val bookDetailsPage = MockResponse().setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
        .setBody(getHtmlString(exampleDetailsPageFile))

    internal fun createJobParameters(): JobParameters =
        JobParametersBuilder()
            .addString("from", from.format(DateTimeFormatter.BASIC_ISO_DATE))
            .addString("to", to.format(DateTimeFormatter.BASIC_ISO_DATE))
            .addLong("seed", Random.nextLong())
            .toJobParameters()

    private fun getHtmlString(filePath: String): String {
        val htmlFile = File(javaClass.classLoader.getResource(filePath)!!.file)
        val reader = BufferedReader(InputStreamReader(FileInputStream(htmlFile), "EUC-KR"))

        return reader.readText()
    }

    internal interface DispatcherOptions {
        fun getResponse(): MockResponse

        fun isValid(request: RecordedRequest): Boolean
    }

    internal class LoginDispatcherOptions(val path: String, private val authenticationProperty: AuthenticationProperty)
        : DispatcherOptions {
        override fun getResponse(): MockResponse = loginSuccessfulResponse

        override fun isValid(request: RecordedRequest): Boolean {
            val queryParams = request.requestUrl!!.toUri().getQueryParams()
            val username = queryParams[KyoboBookRequestNames.username]?.first()
            val password = queryParams[KyoboBookRequestNames.password]?.first()

            val requestPath = request.requestUrl!!.toUri().path
            return if (username == authenticationProperty.kyobo.username && password == authenticationProperty.kyobo.password) {
                path == requestPath
            } else {
                false
            }
        }
    }

    internal class BookDetailsDispatcherOptions(val path: String): DispatcherOptions {
        override fun getResponse(): MockResponse = bookDetailsPage

        override fun isValid(request: RecordedRequest): Boolean {
            val cookies = request.getHeader("Cookie")
            return if (cookies == "$loginCookieKey=$loginCookieValue") {
                request.path == path
            } else {
                false
            }
        }
    }
}