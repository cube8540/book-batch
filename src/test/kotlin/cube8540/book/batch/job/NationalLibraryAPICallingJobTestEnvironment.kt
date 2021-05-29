package cube8540.book.batch.job

import cube8540.book.batch.config.AuthenticationProperty
import cube8540.book.batch.external.nl.go.NationalLibraryAPIRequestNames
import cube8540.book.batch.getQueryParams
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object NationalLibraryAPICallingJobTestEnvironment {

    private const val exampleJsonFile = "job/national-library-successful.json"
    private const val exampleEmptyFile = "job/national-library-empty.json"
    private const val exampleContainsNotMappedPublisherFile = "job/national-library-not-mapped-publisher.json"
    private const val exampleIsbnNullFile = "job/national-library-isbn-null.json"
    private const val exampleMergedFile = "job/national-library-merged.json"

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
        private val jobParameter: JobParameters,
        private val page: Int,
        private val authenticationProperty: AuthenticationProperty
    ) {
        fun isValid(request: RecordedRequest): Boolean {
            val requested = URI.create(request.path!!).getQueryParams()
            return request.requestUrl!!.toUri().path == NationalLibraryAPIRequestNames.endpointPath &&
                    requested[NationalLibraryAPIRequestNames.secretKey]?.first() == authenticationProperty.nationalLibrary.key &&
                    requested[NationalLibraryAPIRequestNames.pageSize]?.first() == NationalLibraryAPIJobConfiguration.defaultChunkSize.toString() &&
                    requested[NationalLibraryAPIRequestNames.pageNumber]?.first() == page.toString() &&
                    requested[NationalLibraryAPIRequestNames.ebookYN]?.first() == "N" &&
                    requested[NationalLibraryAPIRequestNames.resultStyle]?.first() == "json" &&
                    requested[NationalLibraryAPIRequestNames.fromKeyword]?.first() == jobParameter.getString("from") &&
                    requested[NationalLibraryAPIRequestNames.toKeyword]?.first() == jobParameter.getString("to") &&
                    requested[NationalLibraryAPIRequestNames.publisherKeyword]?.first() == jobParameter.getString("publisher")
        }
    }
}