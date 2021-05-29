package cube8540.book.batch.external.naver.com

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIErrorResponse
import cube8540.book.batch.external.BookAPIRequest
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.PageDecision
import cube8540.book.batch.external.exception.ErrorCodeExternalExceptionCreator
import cube8540.book.batch.external.exception.InvalidAuthenticationException
import cube8540.book.batch.getQueryParams
import io.mockk.every
import io.mockk.mockk
import okhttp3.mockwebserver.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterEach
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

class NaverBookAPIExchangerTest {

    private val successfulJsonFilePath = "naver-book-api-response-example.json"
    private val errorJsonFilePath = "naver-book-api-error-response-example.json"

    private val successfulJson = getJsonString(successfulJsonFilePath)
    private val errorJson = getJsonString(errorJsonFilePath)

    private val randomPage = Random.nextInt()
    private val randomSize = Random.nextInt()
    private val realRequestedPage = Random.nextInt(1, 999)

    private val from = LocalDate.of(2021,  5, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val publisher = "publisher0000"

    private val clientId = "clientId0000000"
    private val clientSecret = "clientSecret000000"
    private val naverAPIKey = NaverBookAPIKey(clientId, clientSecret)

    private val mockWebServer = MockWebServer()
    private val publisherMapper: PublisherRawMapper = mockk(relaxed = true)

    private val pageDecision: PageDecision = mockk(relaxed = true)
    private val exceptionCreator: ErrorCodeExternalExceptionCreator = mockk(relaxed = true)
    private val objectMapper = ObjectMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(BookAPIResponse::class.java, NaverBookAPIDeserializer(publisherMapper))
                .addDeserializer(BookAPIErrorResponse::class.java, NaverBookAPIErrorDeserializer())
        )
    private val httpClient = HttpClient.create()
        .responseTimeout(Duration.ofSeconds(1.toLong()))
    private val webClient = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .exchangeStrategies(
            ExchangeStrategies.builder().codecs {
                it.customCodecs().register(Jackson2JsonEncoder(objectMapper))
                it.customCodecs().register(Jackson2JsonDecoder(objectMapper))
            }.build()
        )
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .build()

    private val exchanger = NaverBookAPIExchanger(webClient, naverAPIKey)

    init {
        exchanger.pageDecision = pageDecision
        exchanger.exceptionCreator = exceptionCreator
    }

    @Test
    fun `exchange book api`() {
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = publisher)

        every { pageDecision.calculation(randomPage, randomSize) } returns realRequestedPage
        configSuccessfulHttpResponse(exchangeParameter)

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result).isNotNull
    }

    @Test
    fun `exchange book api when requested page greater then 999`() {
        val page = 1000
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = publisher)

        every { pageDecision.calculation(randomPage, randomSize) } returns page

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result.books).isEmpty()
    }

    @Test
    fun `retry timeout`() {
        val randomRetryCount = Random.nextInt(2, 5)
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = publisher)

        exchanger.retryCount = randomRetryCount
        exchanger.retryDelaySecond = 0

        every { pageDecision.calculation(randomPage, randomSize) } returns realRequestedPage
        mockWebServer.dispatcher = QueueDispatcher()
        IntRange(0, randomRetryCount - 1).forEach { _ ->
            mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))
        }
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(successfulJson))

        val result = exchanger.exchange(exchangeParameter)
        assertThat(result).isNotNull
    }

    @Test
    fun `exchange book when api return error`() {
        val exchangeParameter = BookAPIRequest(page = randomPage, size = randomSize, from = from, to = to, publisher = publisher)

        configErrorHttpResponse(exchangeParameter)
        every { pageDecision.calculation(randomPage, randomSize) } returns realRequestedPage
        every { exceptionCreator.create("024", "Not Exist Client ID : Authentication failed. (인증에 실패했습니다.)") } returns
                InvalidAuthenticationException("Not Exist Client ID : Authentication failed. (인증에 실패했습니다.)")

        val thrown = catchThrowable { exchanger.exchange(exchangeParameter) }
        assertThat(thrown).isInstanceOf(InvalidAuthenticationException::class.java)
        assertThat((thrown as InvalidAuthenticationException).message).isEqualTo("Not Exist Client ID : Authentication failed. (인증에 실패했습니다.)")
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

    private fun configErrorHttpResponse(parameter: BookAPIRequest) {
        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = if (isEqualParams(request, parameter)) {
                MockResponse().setResponseCode(400).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(errorJson)
            } else {
                MockResponse().setResponseCode(200)
            }
        }
    }

    private fun isEqualParams(request: RecordedRequest, params: BookAPIRequest): Boolean {
        val requested = URI.create(request.path!!).getQueryParams()
        return request.requestUrl!!.toUri().path == NaverBookAPIRequestNames.endpointPath &&
                request.headers[NaverBookAPIRequestNames.clientId] == clientId &&
                request.headers[NaverBookAPIRequestNames.clientSecret] == clientSecret &&
                requested[NaverBookAPIRequestNames.fromKeyword]?.first() == params.from!!.format(DateTimeFormatter.BASIC_ISO_DATE) &&
                requested[NaverBookAPIRequestNames.toKeyword]?.first() == params.to!!.format(DateTimeFormatter.BASIC_ISO_DATE) &&
                requested[NaverBookAPIRequestNames.display]?.first() == params.size.toString() &&
                requested[NaverBookAPIRequestNames.start]?.first() == realRequestedPage.toString() &&
                requested[NaverBookAPIRequestNames.publisherKeyword]?.first() == params.publisher &&
                requested[NaverBookAPIRequestNames.isbnKeyword]?.first() == params.isbn
    }

    private fun getJsonString(filePath: String): String {
        val jsonFile = File(javaClass.classLoader.getResource(filePath)!!.file)
        val builder = StringBuilder()
        val reader = Files.newBufferedReader(jsonFile.toPath())

        reader.readLines().forEach { builder.append(it) }

        return builder.toString()
    }

}