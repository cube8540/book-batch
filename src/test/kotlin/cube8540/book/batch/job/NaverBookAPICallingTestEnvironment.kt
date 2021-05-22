package cube8540.book.batch.job

import cube8540.book.batch.config.AuthenticationProperty
import cube8540.book.batch.infra.naver.com.NaverBookAPIRequestNames
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.io.File
import java.nio.file.Files
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object NaverBookAPICallingTestEnvironment {

    private const val exampleJsonFile = "job/naver-book-api-successful.json"
    private const val exampleEmptyFile = "job/naver-book-api-empty.json"
    private const val exampleContainsNotMappedPublisherFile = "job/naver-book-api-publisher-not-mapped.json"
    private const val exampleIsbnNullFile = "job/naver-book-api-isbn-null.json"
    private const val exampleMergedFile = "job/naver-book-api-merged.json"

    internal const val publisherCode = "publisherCode0001"

    private val publishStartDateJobParameter = LocalDate.of(2021, 1, 1)
    private val publishEndDateJobParameter = LocalDate.of(2021, 5, 31)

    internal val successfulResponse = MockResponse().setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody(getJsonString(exampleJsonFile))
    internal val mergedResponse = MockResponse().setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody(getJsonString(exampleMergedFile))
    internal val hasNotMappedPublisherResponse = MockResponse().setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody(getJsonString(exampleContainsNotMappedPublisherFile))
    internal val hasIsbnNullResponse = MockResponse().setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody(getJsonString(exampleIsbnNullFile))
    internal val emptyResponse = MockResponse().setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody(getJsonString(exampleEmptyFile))

    internal fun createJobParameters(): JobParameters =
        JobParametersBuilder()
            .addString("from", publishStartDateJobParameter.format(DateTimeFormatter.BASIC_ISO_DATE))
            .addString("to", publishEndDateJobParameter.format(DateTimeFormatter.BASIC_ISO_DATE))
            .addString("isbn", "")
            .addString("publisher", publisherCode)
            .addLong("seed", Random.nextLong())
            .toJobParameters()

    internal fun getJsonString(filePath: String): String {
        val jsonFile = File(javaClass.classLoader.getResource(filePath)!!.file)
        val builder = StringBuilder()
        val reader = Files.newBufferedReader(jsonFile.toPath())

        reader.readLines().forEach { builder.append(it) }

        return builder.toString()
    }

    internal class DispatcherOptions(
        val response: MockResponse,
        val path: String,
        private val authenticationProperty: AuthenticationProperty
    ) {
        fun isValid(request: RecordedRequest): Boolean {
            val clientId = request.headers[NaverBookAPIRequestNames.clientId]
            val clientSecret = request.headers[NaverBookAPIRequestNames.clientSecret]
            return if (clientId == authenticationProperty.naverBook.clientId && clientSecret == authenticationProperty.naverBook.clientSecret) {
                path == request.path
            } else {
                false
            }
        }
    }
}