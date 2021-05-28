package cube8540.book.batch.scheduler.application

import cube8540.book.batch.scheduler.domain.JobSchedulerReservationStatus
import cube8540.book.batch.scheduler.domain.repository.JobSchedulerReservationRepository
import org.springframework.batch.core.JobExecution
import org.springframework.stereotype.Service

@Service
class DefaultJobSchedulerReservationService(private val repository: JobSchedulerReservationRepository): JobSchedulerReservationService {

    override fun getReservation(): JobSchedulerReservationDetails? =
        repository.findTopReservation()?.let { JobSchedulerReservationDetails.of(it) }

    override fun getReservation(id: Long): JobSchedulerReservationDetails? =
        repository.findDetailsById(id)?.let { JobSchedulerReservationDetails.of(it) }

    override fun addResult(id: Long, jobExecution: JobExecution) {
        repository.findDetailsById(id)?.apply { addResult(jobExecution) }?.let { repository.save(it) }
    }

    override fun updateStatus(reservationId: Long, status: JobSchedulerReservationStatus) {
        repository.findDetailsById(reservationId)?.apply { this.status = status }?.let { repository.save(it) }
    }
}