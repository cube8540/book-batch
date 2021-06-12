package cube8540.book.batch.scheduler.application

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.domain.Publisher
import cube8540.book.batch.book.domain.RawProperty
import cube8540.book.batch.book.repository.PublisherRepository
import cube8540.book.batch.job.JobParameterNames
import cube8540.book.batch.scheduler.domain.JobSchedulerFinishedEvent
import cube8540.book.batch.toDefaultInstance
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.ApplicationEventPublisher
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

    private val eventPublisher: ApplicationEventPublisher = mockk(relaxed = true)

    private val service = LocalDateWithPublisherSchedulerService(mappingType, publisherRepository)

    init {
        LocalDateWithPublisherSchedulerService.clock = Clock.fixed(now.toDefaultInstance(), BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
        service.job = job
        service.jobLauncher = jobLauncher
        service.eventPublisher = eventPublisher
    }

    @Test
    fun `job launched`() {
        val publisherQueryResults = listOf(publisher0, publisher1)
        val launchParameter = JobSchedulerLaunchParameter(from, to)
        val jobParameter0 = createExceptedJobParameter(from, to, "keyword0")
        val execution0: JobExecution = mockk(relaxed = true)
        val jobParameter1 = createExceptedJobParameter(from, to, "keyword1")
        val execution1: JobExecution = mockk(relaxed = true)
        val jobParameter2 = createExceptedJobParameter(from, to, "keyword2")
        val execution2: JobExecution = mockk(relaxed = true)
        val jobParameter3 = createExceptedJobParameter(from, to, "keyword3")
        val execution3: JobExecution = mockk(relaxed = true)
        val jobParameter4 = createExceptedJobParameter(from, to, "keyword4")
        val execution4: JobExecution = mockk(relaxed = true)
        val jobParameter5 = createExceptedJobParameter(from, to, "keyword5")
        val execution5: JobExecution = mockk(relaxed = true)

        every { publisherRepository.findByMappingTypeWithKeyword(mappingType) } returns publisherQueryResults
        every { jobLauncher.run(job, jobParameter0) } returns execution0
        every { jobLauncher.run(job, jobParameter1) } returns execution1
        every { jobLauncher.run(job, jobParameter2) } returns execution2
        every { jobLauncher.run(job, jobParameter3) } returns execution3
        every { jobLauncher.run(job, jobParameter4) } returns execution4
        every { jobLauncher.run(job, jobParameter5) } returns execution5

        service.launchBookDetailsRequest(launchParameter)
        verify {
            jobLauncher.run(job, jobParameter0)
            eventPublisher.publishEvent(JobSchedulerFinishedEvent(execution0, launchParameter))

            jobLauncher.run(job, jobParameter1)
            eventPublisher.publishEvent(JobSchedulerFinishedEvent(execution1, launchParameter))

            jobLauncher.run(job, jobParameter2)
            eventPublisher.publishEvent(JobSchedulerFinishedEvent(execution2, launchParameter))

            jobLauncher.run(job, jobParameter3)
            eventPublisher.publishEvent(JobSchedulerFinishedEvent(execution3, launchParameter))

            jobLauncher.run(job, jobParameter4)
            eventPublisher.publishEvent(JobSchedulerFinishedEvent(execution4, launchParameter))

            jobLauncher.run(job, jobParameter5)
            eventPublisher.publishEvent(JobSchedulerFinishedEvent(execution5, launchParameter))
        }
    }

    private fun createExceptedJobParameter(from: LocalDate, to: LocalDate, publisher: String) = JobParametersBuilder()
        .addString(JobParameterNames.from, from.format(DateTimeFormatter.BASIC_ISO_DATE))
        .addString(JobParameterNames.to, to.format(DateTimeFormatter.BASIC_ISO_DATE))
        .addString(JobParameterNames.publisher, publisher)
        .addString(JobParameterNames.startup, now.format(DateTimeFormatter.ISO_DATE_TIME))
        .toJobParameters()

}