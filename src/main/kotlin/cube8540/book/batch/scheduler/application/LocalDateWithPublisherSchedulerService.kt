package cube8540.book.batch.scheduler.application

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.repository.PublisherRepository
import cube8540.book.batch.job.JobParameterNames
import cube8540.book.batch.scheduler.domain.JobSchedulerFinishedEvent
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.ApplicationEventPublisher
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateWithPublisherSchedulerService(
    private val mappingType: MappingType,
    private val publisherRepository: PublisherRepository
): JobSchedulerService {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
    }

    lateinit var job: Job

    lateinit var jobLauncher: JobLauncher

    var eventPublisher: ApplicationEventPublisher? = null

    override fun launchBookDetailsRequest(jobParameter: JobSchedulerLaunchParameter) {
        val keywords = publisherRepository
            .findByMappingTypeWithKeyword(mappingType).flatMap { it.keywords }.map { it.raw }

        keywords.forEach {
            val parameter = JobParametersBuilder()
                .addString(JobParameterNames.from, jobParameter.from.format(DateTimeFormatter.BASIC_ISO_DATE))
                .addString(JobParameterNames.to, jobParameter.to.format(DateTimeFormatter.BASIC_ISO_DATE))
                .addString(JobParameterNames.publisher, it)
                .addString(JobParameterNames.startup, LocalDateTime.now(clock).format(DateTimeFormatter.ISO_DATE_TIME))
                .toJobParameters()

            val execution = jobLauncher.run(job, parameter)
            eventPublisher?.publishEvent(JobSchedulerFinishedEvent(execution, jobParameter))
        }
    }
}