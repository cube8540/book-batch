package cube8540.book.batch.scheduler.application

import cube8540.book.batch.BatchApplication
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

class LocalDateJobSchedulerServiceTest {

    private val from = LocalDate.of(2021, 1, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val now = LocalDateTime.of(2021, 5, 23, 13, 33, 0)

    private val job: Job = mockk(relaxed = true)
    private val jobLauncher: JobLauncher = mockk(relaxed = true)

    private val eventPublisher: ApplicationEventPublisher = mockk(relaxed = true)

    private val service = LocalDateJobSchedulerService(job, jobLauncher)

    init {
        LocalDateJobSchedulerService.clock = Clock.fixed(now.toDefaultInstance(), BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
        service.eventPublisher = eventPublisher
    }

    @Test
    fun `job launched`() {
        val execution: JobExecution = mockk(relaxed = true)
        val jobParameter = createExceptedJobParameter(from, to)

        every { jobLauncher.run(job, jobParameter) } returns execution

        service.launchBookDetailsRequest(from, to)
        verify { jobLauncher.run(job,  jobParameter) }
        verify { eventPublisher.publishEvent(JobSchedulerFinishedEvent(execution)) }
    }

    private fun createExceptedJobParameter(from: LocalDate, to: LocalDate) = JobParametersBuilder()
        .addString(JobParameterNames.from, from.format(DateTimeFormatter.BASIC_ISO_DATE))
        .addString(JobParameterNames.to, to.format(DateTimeFormatter.BASIC_ISO_DATE))
        .addString(JobParameterNames.startup, now.format(DateTimeFormatter.ISO_DATE_TIME))
        .toJobParameters()

}