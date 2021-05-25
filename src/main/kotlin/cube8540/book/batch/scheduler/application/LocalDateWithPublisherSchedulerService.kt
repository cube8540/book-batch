package cube8540.book.batch.scheduler.application

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.repository.PublisherRepository
import cube8540.book.batch.job.JobParameterNames
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import java.time.Clock
import java.time.LocalDate
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

    override fun launchBookDetailsRequest(from: LocalDate, to: LocalDate) {
        val keywords = publisherRepository
            .findByMappingTypeWithKeyword(mappingType).flatMap { it.keywords }.map { it.raw }

        keywords.forEach {
            val jobParameter = JobParametersBuilder()
                .addString(JobParameterNames.from, from.format(DateTimeFormatter.BASIC_ISO_DATE))
                .addString(JobParameterNames.to, to.format(DateTimeFormatter.BASIC_ISO_DATE))
                .addString(JobParameterNames.publisher, it)
                .addString(JobParameterNames.startup, LocalDateTime.now(clock).format(DateTimeFormatter.ISO_DATE_TIME))
                .toJobParameters()
            jobLauncher.run(job, jobParameter)
        }
    }
}