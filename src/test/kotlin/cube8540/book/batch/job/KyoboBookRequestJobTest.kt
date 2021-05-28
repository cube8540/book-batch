package cube8540.book.batch.job

import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.firstValue
import com.nhaarman.mockitokotlin2.secondValue
import com.nhaarman.mockitokotlin2.times
import cube8540.book.batch.config.AuthenticationProperty
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.QBookDetails
import cube8540.book.batch.domain.Thumbnail
import cube8540.book.batch.domain.repository.BookDetailsRepository
import cube8540.book.batch.external.kyobo.kr.KyoboBookRequestNames
import cube8540.book.batch.job.KyoboBookRequestJobTestEnvironment.BookDetailsDispatcherOptions
import cube8540.book.batch.job.KyoboBookRequestJobTestEnvironment.DispatcherOptions
import cube8540.book.batch.job.KyoboBookRequestJobTestEnvironment.LoginDispatcherOptions
import cube8540.book.batch.job.KyoboBookRequestJobTestEnvironment.createJobParameters
import cube8540.book.batch.job.KyoboBookRequestJobTestEnvironment.from
import cube8540.book.batch.job.KyoboBookRequestJobTestEnvironment.to
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
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import java.net.URI

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class KyoboBookRequestJobTest constructor(
    @Qualifier("kyoboBookRequest")
    private val job: Job,

    jobLauncher: JobLauncher,

    private val authenticationProperty: AuthenticationProperty
) {

    @MockBean
    lateinit var bookDetailsRepository: BookDetailsRepository

    @Captor
    lateinit var persistCaptor: ArgumentCaptor<Collection<BookDetails>>

    private val mockWebServer: MockWebServer = MockWebServer()

    private val jobLauncherTestUtils = JobLauncherTestUtils()

    init {
        jobLauncherTestUtils.job = job
        jobLauncherTestUtils.jobLauncher = jobLauncher

        KyoboBookRequestJobConfiguration.host = mockWebServer.url("/").toUri()
    }

    @Test
    fun `request kyobo book details`() {
        val sort = Sort.by(Sort.Order.desc(QBookDetails.bookDetails.publishDate.metadata.name))
        val jobParameters = createJobParameters()
        val bookDetails: BookDetails = mockk(relaxed = true) {
            every { isbn } returns "9791133447831"
            every { thumbnail } returns null
        }
        val queryResultContents = listOf(bookDetails)
        val firstPageRequest = PageRequest.of(0, KyoboBookRequestJobConfiguration.defaultChunkSize, sort)
        val secondPageRequest = PageRequest.of(1, KyoboBookRequestJobConfiguration.defaultChunkSize, sort)
        val dispatcherOptions = listOf(
            LoginDispatcherOptions(KyoboBookRequestNames.loginUrl, authenticationProperty),
            BookDetailsDispatcherOptions(KyoboBookRequestNames.kyoboBookDetailsPath + "?${KyoboBookRequestNames.isbn}=9791133447831")
        )

        configSuccessfulHttpResponse(dispatcherOptions)
        Mockito.`when`(bookDetailsRepository.findByPublishDateBetween(from, to, firstPageRequest))
            .thenReturn(PageImpl(queryResultContents, firstPageRequest, 1))
        Mockito.`when`(bookDetailsRepository.findByPublishDateBetween(from, to, secondPageRequest))
            .thenReturn(PageImpl(emptyList(), secondPageRequest, 0))
        Mockito.`when`(bookDetailsRepository.findById(listOf("9791133447831")))
            .thenReturn(queryResultContents)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        Mockito.verify(bookDetailsRepository, times(2)).saveAll(capture(persistCaptor))

        val mergedBook = persistCaptor.secondValue.toList()
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(mergedBook.size).isEqualTo(queryResultContents.size)
        assertThat(mergedBook[0]).isEqualTo(bookDetails)
        io.mockk.verify {
            bookDetails.title = "학교생활!. 9"
            bookDetails.thumbnail = Thumbnail(
                largeThumbnail = URI.create("http://image.kyobobook.co.kr/images/book/xlarge/831/x9791133447831.jpg"),
                mediumThumbnail = URI.create("http://image.kyobobook.co.kr/images/book/large/831/l9791133447831.jpg"),
                smallThumbnail = null
            )
            bookDetails.authors = setOf("Norimitsu Kaihou (원작)").toMutableSet()
            bookDetails.seriesCode = "5800068780426"
            bookDetails.price = 5000.toDouble()
        }
    }

    private fun configSuccessfulHttpResponse(options: Collection<DispatcherOptions>) {
        mockWebServer.dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse =
                options.find { it.isValid(request) }?.getResponse()
                    ?: MockResponse().setResponseCode(404)
        }
    }

}