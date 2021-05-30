package cube8540.book.batch.job

import com.nhaarman.mockitokotlin2.*
import cube8540.book.batch.AuthenticationProperty
import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsFilterFunction
import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.book.repository.BookDetailsRepository
import cube8540.book.batch.job.NationalLibraryAPICallingJobTestEnvironment.DispatcherOptions
import cube8540.book.batch.job.NationalLibraryAPICallingJobTestEnvironment.createJobParameters
import cube8540.book.batch.job.NationalLibraryAPICallingJobTestEnvironment.emptyResponse
import cube8540.book.batch.job.NationalLibraryAPICallingJobTestEnvironment.hasIsbnNullResponse
import cube8540.book.batch.job.NationalLibraryAPICallingJobTestEnvironment.hasNotMappedPublisherResponse
import cube8540.book.batch.job.NationalLibraryAPICallingJobTestEnvironment.mergedResponse
import cube8540.book.batch.job.NationalLibraryAPICallingJobTestEnvironment.publisherCode
import cube8540.book.batch.job.NationalLibraryAPICallingJobTestEnvironment.successfulResponse
import io.mockk.every
import io.mockk.mockk
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class NationalLibraryAPICallingJobTest constructor(
    @Qualifier(value = NationalLibraryAPIJobConfiguration.jobName)
    private val job: Job,

    jobLauncher: JobLauncher,

    private val authenticationProperty: AuthenticationProperty
) {

    @MockBean(name = "nationalLibraryPublisherRawMapper")
    @Qualifier("nationalLibraryPublisherRawMapper")
    lateinit var publisherRawMapper: PublisherRawMapper

    @MockBean
    lateinit var bookDetailsRepository: BookDetailsRepository

    @MockBean(name = "nationalLibraryFilterFunction")
    @Qualifier("nationalLibraryFilterFunction")
    lateinit var operatorMapper: BookDetailsFilterFunction

    @Captor
    lateinit var persistCaptor: ArgumentCaptor<Collection<BookDetails>>

    private val jobLauncherTestUtils = JobLauncherTestUtils()

    private val mockWebServer: MockWebServer = MockWebServer()

    init {
        jobLauncherTestUtils.job = job
        jobLauncherTestUtils.jobLauncher = jobLauncher
        NationalLibraryAPIJobConfiguration.endpointBase = mockWebServer.url("/").toString()
    }

    @Test
    fun `api result has not mapped publisher`() {
        val jobParameters = createJobParameters()

        val dispatcherOptions = listOf(
            DispatcherOptions(hasNotMappedPublisherResponse, jobParameters, 1, authenticationProperty),
            DispatcherOptions(emptyResponse, jobParameters, 2, authenticationProperty)
        )
        Mockito.`when`(publisherRawMapper.mapping("대원씨아이(주)")).thenReturn(publisherCode)
        Mockito.`when`(publisherRawMapper.mapping("NOT MATCHED PUBLISHER")).thenReturn(null)
        Mockito.`when`(operatorMapper.isValid(any())).thenReturn(true)
        configSuccessfulHttpResponse(dispatcherOptions)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        Mockito.verify(bookDetailsRepository, times(2)).saveAll(capture(persistCaptor))

        val insertedBook = persistCaptor.firstValue.toList()[0]
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(insertedBook.isbn).isEqualTo("2676865437795")
    }

    @Test
    fun `api isbn null`() {
        val jobParameters = createJobParameters()

        val dispatcherOptions = listOf(
            DispatcherOptions(hasIsbnNullResponse, jobParameters, 1, authenticationProperty),
            DispatcherOptions(emptyResponse, jobParameters, 2, authenticationProperty)
        )
        Mockito.`when`(publisherRawMapper.mapping("대원씨아이(주)")).thenReturn(publisherCode)
        Mockito.`when`(operatorMapper.isValid(any())).thenReturn(true)
        configSuccessfulHttpResponse(dispatcherOptions)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        Mockito.verify(bookDetailsRepository, times(2)).saveAll(capture(persistCaptor))

        val insertedBook = persistCaptor.firstValue.toList()[0]
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(insertedBook.isbn).isEqualTo("2676865437795")
    }

    @Test
    fun `filtering book details`() {
        val jobParameters = createJobParameters()

        val dispatcherOptions = listOf(
            DispatcherOptions(successfulResponse, jobParameters, 1, authenticationProperty),
            DispatcherOptions(emptyResponse, jobParameters, 2, authenticationProperty)
        )
        Mockito.`when`(publisherRawMapper.mapping("대원씨아이(주)")).thenReturn(publisherCode)
        Mockito.`when`(operatorMapper.isValid(any())).thenReturn(false)
        configSuccessfulHttpResponse(dispatcherOptions)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        Mockito.verify(bookDetailsRepository, never()).saveAll(Mockito.anyList())
    }

    @Test
    fun `call national library api`() {
        val jobParameters = createJobParameters()

        val dispatcherOptions = listOf(
            DispatcherOptions(successfulResponse, jobParameters, 1, authenticationProperty),
            DispatcherOptions(emptyResponse, jobParameters, 2, authenticationProperty)
        )
        Mockito.`when`(publisherRawMapper.mapping("대원씨아이(주)")).thenReturn(publisherCode)
        Mockito.`when`(operatorMapper.isValid(any())).thenReturn(true)
        configSuccessfulHttpResponse(dispatcherOptions)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        Mockito.verify(bookDetailsRepository, times(2)).saveAll(capture(persistCaptor))

        val insertedBooks = persistCaptor.firstValue.toList()
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(insertedBooks[0].isbn).isEqualTo("1013791591417")
        assertThat(insertedBooks[1].isbn).isEqualTo("2676865437795")
        assertThat(insertedBooks[2].isbn).isEqualTo("1795818571716")
    }

    @Test
    fun `merge book details`() {
        val jobParameters = createJobParameters()
        val storedBookDetails: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "1013791591417"
        }

        val dispatcherOptions = listOf(
            DispatcherOptions(successfulResponse, jobParameters, 1, authenticationProperty),
            DispatcherOptions(emptyResponse, jobParameters, 2, authenticationProperty)
        )
        Mockito.`when`(publisherRawMapper.mapping("대원씨아이(주)")).thenReturn(publisherCode)
        Mockito.`when`(operatorMapper.isValid(any())).thenReturn(true)
        Mockito.`when`(bookDetailsRepository.findById(listOf("1013791591417", "2676865437795")))
            .thenReturn(listOf(storedBookDetails))
        configSuccessfulHttpResponse(dispatcherOptions)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        Mockito.verify(bookDetailsRepository).saveAll(emptyList())
    }

    @Test
    fun `create new book details`() {
        val jobParameters = createJobParameters()
        val storedBookDetails: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "1013791591417"
        }

        val dispatcherOptions = listOf(
            DispatcherOptions(mergedResponse, jobParameters, 1, authenticationProperty),
            DispatcherOptions(emptyResponse, jobParameters, 2, authenticationProperty)
        )
        Mockito.`when`(publisherRawMapper.mapping("대원씨아이(주)")).thenReturn(publisherCode)
        Mockito.`when`(operatorMapper.isValid(any())).thenReturn(true)
        Mockito.`when`(bookDetailsRepository.findById(listOf("1013791591417", "2676865437795")))
            .thenReturn(listOf(storedBookDetails))
        configSuccessfulHttpResponse(dispatcherOptions)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        Mockito.verify(bookDetailsRepository, times(2)).saveAll(capture(persistCaptor))

        val insertedBook = persistCaptor.firstValue.toList()[0]
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(insertedBook.isbn).isEqualTo("2676865437795")
    }

    @AfterEach
    fun cleanup() {
        mockWebServer.shutdown()
        mockWebServer.close()
    }

    private fun configSuccessfulHttpResponse(options: Collection<DispatcherOptions>) {
        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse =
                options.find { it.isValid(request) }?.response
                    ?: MockResponse().setResponseCode(404)
        }
    }

}