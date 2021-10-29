package cube8540.book.batch.scheduler

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.scheduler.application.JobSchedulerLaunchParameter
import cube8540.book.batch.scheduler.application.JobSchedulerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDate

@Component
@Profile(value = ["local"])
class LocalJobSchedulerConfiguration {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
    }

    @set:[Autowired Qualifier("regularJobSchedulerService")]
    lateinit var regularJobSchedulerService: JobSchedulerService

    @Scheduled(initialDelay = 1500, fixedDelay = 3600000)
    fun launchJob() {
        val from = LocalDate.now(clock).minusDays(3)
        val to = LocalDate.now(clock).plusMonths(3)

        val launchParameter = JobSchedulerLaunchParameter(from, to)
        regularJobSchedulerService.launchBookDetailsRequest(launchParameter)
    }
}