package cube8540.book.batch.scheduler.application

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.Publisher
import cube8540.book.batch.domain.RawProperty
import cube8540.book.batch.domain.repository.PublisherRepository
import cube8540.book.batch.job.JobParameterNames
import cube8540.book.batch.toDefaultInstance
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class LocalDateWithPublisherSchedulerServiceTest {

    private val mappingType = MappingType.values()[Random.nextInt(0, MappingType.values().size)]

    private val publisher0: Publisher = mockk(relaxed = true) {
        every { keywords } returns setOf(
            RawProperty("keyword0", mappingType),
            RawProperty("keyword1", mappingType),
            RawProperty("keyword2", mappingType)
        )
    }
    private val publisher1: Publisher = mockk(relaxed = true) {
        every { keywords } returns setOf(
            RawProperty("keyword3", mappingType),
            RawProperty("keyword4", mappingType),
            RawProperty("keyword5", mappingType)
        )
    }

    private val from = LocalDate.of(2021, 1, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val now = LocalDateTime.of(2021, 5, 23, 13, 33, 0)

    private val job: Job = mockk(relaxed = true)
    private val jobLauncher: JobLauncher = mockk(relaxed = true)
    private val publisherRepository: PublisherRepository = mockk(relaxed = true)

    private val service = LocalDateWithPublisherSchedulerService(mappingType, publisherRepository)


    init {
        LocalDateWithPublisherSchedulerService.clock = Clock.fixed(now.toDefaultInstance(), BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
        service.job = job
        service.jobLauncher = jobLauncher
    }

    @Test
    fun `job launched`() {
        val publisherQueryResults = listOf(publisher0, publisher1)

        every { publisherRepository.findByMappingTypeWithKeyword(mappingType) } returns publisherQueryResults

        service.launchBookDetailsRequest(from, to)
        verify {
            jobLauncher.run(job, createExceptedJobParameter(from, to, "keyword0"))
            jobLauncher.run(job, createExceptedJobParameter(from, to, "keyword1"))
            jobLauncher.run(job, createExceptedJobParameter(from, to, "keyword2"))
            jobLauncher.run(job, createExceptedJobParameter(from, to, "keyword3"))
            jobLauncher.run(job, createExceptedJobParameter(from, to, "keyword4"))
            jobLauncher.run(job, createExceptedJobParameter(from, to, "keyword5"))
        }
    }

    private fun createExceptedJobParameter(from: LocalDate, to: LocalDate, publisher: String) = JobParametersBuilder()
        .addString(JobParameterNames.from, from.format(DateTimeFormatter.BASIC_ISO_DATE))
        .addString(JobParameterNames.to, to.format(DateTimeFormatter.BASIC_ISO_DATE))
        .addString(JobParameterNames.publisher, publisher)
        .addString(JobParameterNames.startup, now.format(DateTimeFormatter.ISO_DATE_TIME))
        .toJobParameters()

}