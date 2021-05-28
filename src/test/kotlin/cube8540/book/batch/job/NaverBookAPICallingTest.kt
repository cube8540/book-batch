package cube8540.book.batch.job

import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.firstValue
import com.nhaarman.mockitokotlin2.times
import cube8540.book.batch.config.AuthenticationProperty
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.domain.Thumbnail
import cube8540.book.batch.domain.repository.BookDetailsRepository
import cube8540.book.batch.external.naver.com.NaverBookAPIRequestNames
import cube8540.book.batch.job.NaverBookAPICallingTestEnvironment.DispatcherOptions
import cube8540.book.batch.job.NaverBookAPICallingTestEnvironment.createJobParameters
import cube8540.book.batch.job.NaverBookAPICallingTestEnvironment.emptyResponse
import cube8540.book.batch.job.NaverBookAPICallingTestEnvironment.hasIsbnNullResponse
import cube8540.book.batch.job.NaverBookAPICallingTestEnvironment.hasNotMappedPublisherResponse
import cube8540.book.batch.job.NaverBookAPICallingTestEnvironment.mergedResponse
import cube8540.book.batch.job.NaverBookAPICallingTestEnvironment.publisherCode
import cube8540.book.batch.job.NaverBookAPICallingTestEnvironment.successfulResponse
import io.mockk.every
import io.mockk.mockk
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class NaverBookAPICallingTest constructor(
    @Qualifier("naverBookAPICalling")
    private val job: Job,

    jobLauncher: JobLauncher,

    private val authenticationProperty: AuthenticationProperty
) {

    @MockBean(name = "naverPublisherRawMapper")
    @Qualifier("naverPublisherRawMapper")
    lateinit var publisherRawMapper: PublisherRawMapper

    @MockBean
    lateinit var bookDetailsRepository: BookDetailsRepository

    @Captor
    lateinit var persistCaptor: ArgumentCaptor<Collection<BookDetails>>

    private val jobLauncherTestUtils = JobLauncherTestUtils()

    private val mockWebServer: MockWebServer = MockWebServer()
    private val endpoint: String = mockWebServer.url("/").toString()

    init {
        jobLauncherTestUtils.job = job
        jobLauncherTestUtils.jobLauncher = jobLauncher
        NaverBookAPIJobConfiguration.endpoint = URI.create(endpoint)
    }

    @Test
    fun `api result has not mapped publisher`() {
        val jobParameters = createJobParameters()

        val dispatcherOptions = listOf(
            DispatcherOptions(hasNotMappedPublisherResponse, createExpectedUri(jobParameters, 1), authenticationProperty),
            DispatcherOptions(emptyResponse, createExpectedUri(jobParameters, NaverBookAPIJobConfiguration.defaultChunkSize + 1), authenticationProperty)
        )
        Mockito.`when`(publisherRawMapper.mapping("<b>대원씨아이</b>")).thenReturn(publisherCode)
        Mockito.`when`(publisherRawMapper.mapping("<b>NOT MAPPED PUBLISHER</b>")).thenReturn(null)
        configSuccessfulHttpResponse(dispatcherOptions)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        Mockito.verify(bookDetailsRepository, times(2)).saveAll(capture(persistCaptor))

        val insertedBook = persistCaptor.firstValue.toList()[0]
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(persistCaptor.firstValue.size).isEqualTo(1)
        assertThat(insertedBook.isbn).isEqualTo("9791136202093")
    }

    @Test
    fun `api isbn null`() {
        val jobParameters = createJobParameters()

        val dispatcherOptions = listOf(
            DispatcherOptions(hasIsbnNullResponse, createExpectedUri(jobParameters, 1), authenticationProperty),
            DispatcherOptions(emptyResponse, createExpectedUri(jobParameters, NaverBookAPIJobConfiguration.defaultChunkSize + 1), authenticationProperty)
        )
        Mockito.`when`(publisherRawMapper.mapping("<b>대원씨아이</b>")).thenReturn(publisherCode)
        configSuccessfulHttpResponse(dispatcherOptions)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        Mockito.verify(bookDetailsRepository, times(2)).saveAll(capture(persistCaptor))

        val insertedBook = persistCaptor.firstValue.toList()[0]
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(persistCaptor.firstValue.size).isEqualTo(1)
        assertThat(insertedBook.isbn).isEqualTo("9791136202093")
    }

    @Test
    fun `call naver book api`() {
        val jobParameters = createJobParameters()

        val dispatcherOptions = listOf(
            DispatcherOptions(successfulResponse, createExpectedUri(jobParameters, 1), authenticationProperty),
            DispatcherOptions(emptyResponse, createExpectedUri(jobParameters, NaverBookAPIJobConfiguration.defaultChunkSize + 1), authenticationProperty)
        )
        Mockito.`when`(publisherRawMapper.mapping("<b>대원씨아이</b>")).thenReturn(publisherCode)
        configSuccessfulHttpResponse(dispatcherOptions)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        Mockito.verify(bookDetailsRepository, times(2)).saveAll(capture(persistCaptor))

        val insertedBooks = persistCaptor.firstValue.toList()
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(insertedBooks.size).isEqualTo(3)
        assertThat(insertedBooks[0].isbn).isEqualTo("9791136226259")
        assertThat(insertedBooks[1].isbn).isEqualTo("9791136202093")
        assertThat(insertedBooks[2].isbn).isEqualTo("9791164120123")
    }

    @Test
    fun `merged book details`() {
        val jobParameter = createJobParameters()
        val storedBookDetails: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "9791136202093"
            every { thumbnail } returns null
        }

        val dispatcherOptions = listOf(
            DispatcherOptions(mergedResponse, createExpectedUri(jobParameter, 1), authenticationProperty),
            DispatcherOptions(emptyResponse, createExpectedUri(jobParameter, NaverBookAPIJobConfiguration.defaultChunkSize + 1), authenticationProperty)
        )
        Mockito.`when`(publisherRawMapper.mapping("<b>대원씨아이</b>")).thenReturn(publisherCode)
        Mockito.`when`(bookDetailsRepository.findById(listOf("9791136202093", "9791164120123"))).thenReturn(listOf(storedBookDetails))
        configSuccessfulHttpResponse(dispatcherOptions)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameter)
        Mockito.verify(bookDetailsRepository, times(2)).saveAll(capture(persistCaptor))

        val mergedBook = persistCaptor.value.toList()[0]
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(mergedBook).isEqualTo(storedBookDetails)
        io.mockk.verify {
            storedBookDetails.title = "<b>학교생활</b> 11 (exists)"
            storedBookDetails.publisher = publisherCode
            storedBookDetails.publishDate = LocalDate.of(2019, 7, 31)
            storedBookDetails.thumbnail = Thumbnail(largeThumbnail = null, mediumThumbnail = null, smallThumbnail = URI.create("https://bookthumb-phinf.pstatic.net/cover/150/764/15076492.jpg?type=m1&udate=20190715"))
        }
    }

    @Test
    fun `create new book details`() {
        val jobParameter = createJobParameters()
        val storedBookDetails: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "9791136202093"
        }

        val dispatcherOptions = listOf(
            DispatcherOptions(mergedResponse, createExpectedUri(jobParameter, 1), authenticationProperty),
            DispatcherOptions(emptyResponse, createExpectedUri(jobParameter, NaverBookAPIJobConfiguration.defaultChunkSize + 1), authenticationProperty)
        )
        Mockito.`when`(publisherRawMapper.mapping("<b>대원씨아이</b>")).thenReturn(publisherCode)
        Mockito.`when`(bookDetailsRepository.findById(listOf("9791136202093", "9791164120123"))).thenReturn(listOf(storedBookDetails))
        configSuccessfulHttpResponse(dispatcherOptions)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameter)
        Mockito.verify(bookDetailsRepository, times(2)).saveAll(capture(persistCaptor))

        val insertedBook = persistCaptor.firstValue.toList()[0]
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(insertedBook.isbn).isEqualTo("9791164120123")
    }

    private fun configSuccessfulHttpResponse(options: Collection<DispatcherOptions>) {
        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse =
                options.find { it.isValid(request) }?.response
                    ?: MockResponse().setResponseCode(404)
        }
    }

    private fun createExpectedUri(jobParameter: JobParameters, page: Int): String {
        val uriBuilder = UriComponentsBuilder.newInstance()
            .uri(URI.create("/"))
            .queryParam(NaverBookAPIRequestNames.fromKeyword, jobParameter.getString("from"))
            .queryParam(NaverBookAPIRequestNames.toKeyword, jobParameter.getString("to"))
            .queryParam(NaverBookAPIRequestNames.publisherKeyword, jobParameter.getString("publisher"))
            .queryParam(NaverBookAPIRequestNames.isbnKeyword, jobParameter.getString("isbn"))
            .queryParam(NaverBookAPIRequestNames.start, page)
            .queryParam(NaverBookAPIRequestNames.display, NaverBookAPIJobConfiguration.defaultChunkSize)
            .encode(StandardCharsets.UTF_8)
        return uriBuilder.toUriString()
    }
}