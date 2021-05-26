package cube8540.book.batch.scheduler.application

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.job.JobParameterNames
import cube8540.book.batch.scheduler.domain.JobSchedulerFinishedEvent
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.ApplicationEventPublisher
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateJobSchedulerService(private val job: Job, private val jobLauncher: JobLauncher): JobSchedulerService {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
    }

    var eventPublisher: ApplicationEventPublisher? = null

    override fun launchBookDetailsRequest(from: LocalDate, to: LocalDate) {
        val jobParameter = JobParametersBuilder()
            .addString(JobParameterNames.from, from.format(DateTimeFormatter.BASIC_ISO_DATE))
            .addString(JobParameterNames.to, to.format(DateTimeFormatter.BASIC_ISO_DATE))
            .addString(JobParameterNames.startup, LocalDateTime.now(clock).format(DateTimeFormatter.ISO_DATE_TIME))
            .toJobParameters()

        val execution = jobLauncher.run(job, jobParameter)
        eventPublisher?.publishEvent(JobSchedulerFinishedEvent(execution))
    }
}