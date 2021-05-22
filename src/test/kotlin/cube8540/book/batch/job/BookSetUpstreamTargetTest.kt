package cube8540.book.batch.job

import com.nhaarman.mockitokotlin2.capture
import cube8540.book.batch.domain.*
import cube8540.book.batch.domain.repository.BookDetailsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookSetUpstreamTargetTest constructor(
    @Qualifier("bookSetUpstreamTarget")
    private val job: Job,

    jobLauncher: JobLauncher
) {

    @MockBean
    lateinit var bookDetailsRepository: BookDetailsRepository

    @MockBean
    @Qualifier("nationalLibraryFilterFunction")
    lateinit var nationalLibraryFilterFunction: BookDetailsFilterFunction

    @MockBean
    @Qualifier("kyoboBookFilterFunction")
    lateinit var kyoboFilterFunction: BookDetailsFilterFunction

    @Captor
    lateinit var persistCaptor: ArgumentCaptor<Collection<BookDetails>>

    private val jobLauncherTestUtils = JobLauncherTestUtils()

    private val from = LocalDate.of(2021, 1, 1)
    private val to = LocalDate.of(2021, 5, 31)

    init {
        jobLauncherTestUtils.job = job
        jobLauncherTestUtils.jobLauncher = jobLauncher
    }

    @Test
    fun `set book details upstream target`() {
        val sort = Sort.by(Sort.Order.desc(QBookDetails.bookDetails.publishDate.metadata.name))
        val jobParameters = createJobParameters()
        val bookDetails: BookDetails = mockk(relaxed = true) {
            every { original } returns mutableMapOf(
                OriginalPropertyKey("property", MappingType.NATIONAL_LIBRARY) to "value",
                OriginalPropertyKey("property", MappingType.KYOBO) to "value"
            )
        }

        val queryResultContents = listOf(bookDetails)
        val firstPageRequest = PageRequest.of(0, BookSetUpstreamTargetJobConfiguration.defaultChunkSize, sort)
        val secondPageRequest = PageRequest.of(1, BookSetUpstreamTargetJobConfiguration.defaultChunkSize, sort)

        Mockito.`when`(bookDetailsRepository.findByPublishDateBetween(from, to, firstPageRequest))
            .thenReturn(PageImpl(queryResultContents, firstPageRequest, 1))
        Mockito.`when`(bookDetailsRepository.findByPublishDateBetween(from, to, secondPageRequest))
            .thenReturn(PageImpl(emptyList(), secondPageRequest, 0))
        Mockito.`when`(nationalLibraryFilterFunction.isValid(bookDetails)).thenReturn(true)
        Mockito.`when`(kyoboFilterFunction.isValid(bookDetails)).thenReturn(true)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        Mockito.verify(bookDetailsRepository).updateForUpstreamTarget(capture(persistCaptor))

        val upstreamTarget = persistCaptor.value.toList()
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(upstreamTarget[0]).isEqualTo(bookDetails)
        verify {
            bookDetails.isUpstreamTarget = true
        }
    }

    @Test
    fun `set book details upstream target when filtering returns false`() {
        val sort = Sort.by(Sort.Order.desc(QBookDetails.bookDetails.publishDate.metadata.name))
        val jobParameters = createJobParameters()
        val bookDetails: BookDetails = mockk(relaxed = true) {
            every { original } returns mutableMapOf(
                OriginalPropertyKey("property", MappingType.NATIONAL_LIBRARY) to "value",
                OriginalPropertyKey("property", MappingType.KYOBO) to "value"
            )
        }

        val queryResultContents = listOf(bookDetails)
        val firstPageRequest = PageRequest.of(0, BookSetUpstreamTargetJobConfiguration.defaultChunkSize, sort)
        val secondPageRequest = PageRequest.of(1, BookSetUpstreamTargetJobConfiguration.defaultChunkSize, sort)

        Mockito.`when`(bookDetailsRepository.findByPublishDateBetween(from, to, firstPageRequest))
            .thenReturn(PageImpl(queryResultContents, firstPageRequest, 1))
        Mockito.`when`(bookDetailsRepository.findByPublishDateBetween(from, to, secondPageRequest))
            .thenReturn(PageImpl(emptyList(), secondPageRequest, 0))
        Mockito.`when`(nationalLibraryFilterFunction.isValid(bookDetails)).thenReturn(false)
        Mockito.`when`(kyoboFilterFunction.isValid(bookDetails)).thenReturn(true)

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        Mockito.verify(bookDetailsRepository).updateForUpstreamTarget(emptyList())
    }

    private fun createJobParameters(): JobParameters =
        JobParametersBuilder()
            .addString("from", KyoboBookRequestJobTestEnvironment.from.format(DateTimeFormatter.BASIC_ISO_DATE))
            .addString("to", KyoboBookRequestJobTestEnvironment.to.format(DateTimeFormatter.BASIC_ISO_DATE))
            .addLong("seed", Random.nextLong())
            .toJobParameters()
}