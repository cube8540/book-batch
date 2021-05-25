package cube8540.book.batch.scheduler

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.scheduler.application.JobSchedulerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import java.time.Clock
import java.time.LocalDate

@Configuration
@Profile("!test")
class JobSchedulerConfiguration {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
    }

    @set:[Autowired Qualifier("nationalLibraryAPIJobSchedulerService")]
    lateinit var nationalLibraryAPIJobSchedulerService: JobSchedulerService

    @set:[Autowired Qualifier("naverBookAPIJobSchedulerService")]
    lateinit var naverBookAPIJobSchedulerService: JobSchedulerService

    @set:[Autowired Qualifier("kyoboBookRequestJobSchedulerService")]
    lateinit var kyoboBookRequestJobSchedulerService: JobSchedulerService

    @set:[Autowired Qualifier("setUpstreamTargetJobSchedulerService")]
    lateinit var setUpstreamTargetJobSchedulerService: JobSchedulerService

    @Scheduled(initialDelay = 1000, fixedDelay = 43200000)
    fun launchJob() {
        val from = LocalDate.now(clock).minusMonths(3)
        val to = LocalDate.now(clock).plusMonths(3)
        nationalLibraryAPIJobSchedulerService.launchBookDetailsRequest(from, to)
        naverBookAPIJobSchedulerService.launchBookDetailsRequest(from, to)
        kyoboBookRequestJobSchedulerService.launchBookDetailsRequest(from, to)
        setUpstreamTargetJobSchedulerService.launchBookDetailsRequest(from, to)
    }
}