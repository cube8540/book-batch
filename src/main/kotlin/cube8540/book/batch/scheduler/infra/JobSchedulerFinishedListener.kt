package cube8540.book.batch.scheduler.infra

import cube8540.book.batch.scheduler.application.JobSchedulerReservationService
import cube8540.book.batch.scheduler.domain.JobSchedulerFinishedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class JobSchedulerFinishedListener(private val service: JobSchedulerReservationService): ApplicationListener<JobSchedulerFinishedEvent> {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun onApplicationEvent(event: JobSchedulerFinishedEvent) {
        event.launchParameter.jobSchedulerReservationId?.let { service.addResult(it, event.jobExecution) }
    }
}