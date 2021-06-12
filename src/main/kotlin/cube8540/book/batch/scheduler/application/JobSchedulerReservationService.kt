package cube8540.book.batch.scheduler.application

import cube8540.book.batch.scheduler.domain.JobSchedulerReservationStatus
import org.springframework.batch.core.JobExecution

interface JobSchedulerReservationService {
    fun getReservation(): JobSchedulerReservationDetails?

    fun getReservation(id: Long): JobSchedulerReservationDetails?

    fun addResult(id: Long, jobExecution: JobExecution)

    fun updateStatus(reservationId: Long, status: JobSchedulerReservationStatus)
}