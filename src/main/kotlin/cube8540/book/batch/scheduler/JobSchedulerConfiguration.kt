package cube8540.book.batch.scheduler

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.scheduler.application.JobSchedulerLaunchParameter
import cube8540.book.batch.scheduler.application.JobSchedulerReservationService
import cube8540.book.batch.scheduler.application.JobSchedulerService
import cube8540.book.batch.scheduler.domain.JobSchedulerReservationStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDate

@Component
@Profile(value = ["!test", "!local"])
class JobSchedulerConfiguration {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
    }

    @set:[Autowired Qualifier("regularJobSchedulerService")]
    lateinit var regularJobSchedulerService: JobSchedulerService

    @set:[Autowired Qualifier("nonRegularJobSchedulerService")]
    lateinit var nonRegularJobSchedulerService: JobSchedulerService

    @set:Autowired
    lateinit var jobSchedulerReservationService: JobSchedulerReservationService

    @Scheduled(cron = "0 0 0,12 * * *", zone = "Asia/Seoul")
    fun launchJob() {
        val from = LocalDate.now(clock).minusMonths(3)
        val to = LocalDate.now(clock).plusMonths(3)

        val launchParameter = JobSchedulerLaunchParameter(from, to)
        regularJobSchedulerService.launchBookDetailsRequest(launchParameter)
    }

    @Scheduled(initialDelay = 1500, fixedDelay = 60000)
    fun launchReservationJob() {
        val reservation = jobSchedulerReservationService.getReservation()
        if (reservation != null) {
            val launchParameter = JobSchedulerLaunchParameter(reservation.from, reservation.to, reservation.reservationId)
            jobSchedulerReservationService.updateStatus(reservation.reservationId, JobSchedulerReservationStatus.PROCESSING)
            nonRegularJobSchedulerService.launchBookDetailsRequest(launchParameter)
            jobSchedulerReservationService.updateStatus(reservation.reservationId, JobSchedulerReservationStatus.COMPLETED)
        }
    }
}