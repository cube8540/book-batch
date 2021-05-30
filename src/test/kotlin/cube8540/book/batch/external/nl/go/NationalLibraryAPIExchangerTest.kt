package cube8540.book.batch.external.nl.go

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIErrorResponse
import cube8540.book.batch.external.BookAPIRequest
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.PageDecision
import cube8540.book.batch.getQueryParams
import io.mockk.every
import io.mockk.mockk
import okhttp3.mockwebserver.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class NationalLibraryAPIExchangerTest {

    private val successfulJsonFilePath = "national-library-response-example.json"

    private val successfulJson = getJsonString(successfulJsonFilePath)

    private val randomPage = Random.nextInt()
    private val randomSize = Random.nextInt()
    private val realRequestedPage = Random.nextInt()

    private val from = LocalDate.of(2021,  5, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val publisher = "publisher0000"

    private val apiKey = "apikey00000001"
    private val nationalLibraryAPIKey = NationalLibraryAPIKey(apiKey)

    private val mockWebServer = MockWebServer()
    private val publisherMapper: PublisherRawMapper = mockk(relaxed = true)

    private val pageDecision: PageDecision = mockk(relaxed = true) {
        every { calculation(randomPage, randomSize) } returns realRequestedPage
    }

    private val objectMapper = ObjectMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(BookAPIResponse::class.java, NationalLibraryAPIDeserializer(publisherMapper))
                .addDeserializer(BookAPIErrorResponse::class.java, NationalLibraryAPIErrorDeserializer())
        )
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
    private val httpClient = HttpClient.create()
        .responseTimeout(Duration.ofSeconds(1.toLong()))
    private val webClient = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .exchangeStrategies(
            ExchangeStrategies.builder().codecs {
                it.customCodecs().register(Jackson2JsonDecoder(objectMapper))
                it.customCodecs().register(Jackson2JsonEncoder(objectMapper))
            }.build()
        ).build()

    private val exchanger = NationalLibraryAPIExchanger(webClient, nationalLibraryAPIKey)

    init {
        exchanger.pageDecision = pageDecision
    }

    @Test
    fun `exchange book api`() {
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = publisher)

        configSuccessfulHttpResponse(exchangeParameter)

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result).isNotNull
    }

    @Test
    fun `retry timeout`() {
        val randomRetryCount = Random.nextInt(2, 5)
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = publisher)

        exchanger.retryCount = randomRetryCount
        exchanger.retryDelaySecond = 0

        mockWebServer.dispatcher = QueueDispatcher()
        IntRange(0, randomRetryCount - 1).forEach { _ ->
            mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))
        }
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(successfulJson))

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result).isNotNull
    }

    private fun configSuccessfulHttpResponse(parameter: BookAPIRequest) {
        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = if (isEqualParams(request, parameter)) {
                MockResponse().setResponseCode(200).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(successfulJson)
            } else {
                MockResponse().setResponseCode(404)
            }
        }
    }

    private fun isEqualParams(request: RecordedRequest, params: BookAPIRequest): Boolean {
        val requested = URI.create(request.path!!).getQueryParams()
        return request.requestUrl!!.toUri().path == NationalLibraryAPIRequestNames.endpointPath &&
                requested[NationalLibraryAPIRequestNames.secretKey]?.first() == apiKey &&
                requested[NationalLibraryAPIRequestNames.pageSize]?.first() == params.size.toString() &&
                requested[NationalLibraryAPIRequestNames.pageNumber]?.first() == realRequestedPage.toString() &&
                requested[NationalLibraryAPIRequestNames.ebookYN]?.first() == "N" &&
                requested[NationalLibraryAPIRequestNames.resultStyle]?.first() == "json" &&
                requested[NationalLibraryAPIRequestNames.fromKeyword]?.first() == params.from!!.format(DateTimeFormatter.BASIC_ISO_DATE) &&
                requested[NationalLibraryAPIRequestNames.toKeyword]?.first() == params.to!!.format(DateTimeFormatter.BASIC_ISO_DATE) &&
                requested[NationalLibraryAPIRequestNames.isbnKeyword]?.first() == params.isbn &&
                requested[NationalLibraryAPIRequestNames.publisherKeyword]?.first() == params.publisher
    }

    private fun getJsonString(filePath: String): String {
        val jsonFile = File(javaClass.classLoader.getResource(filePath)!!.file)
        val builder = StringBuilder()
        val reader = Files.newBufferedReader(jsonFile.toPath())

        reader.readLines().forEach { builder.append(it) }

        return builder.toString()
    }
}