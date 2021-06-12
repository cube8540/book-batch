package cube8540.book.batch.scheduler.domain.repository

import cube8540.book.batch.scheduler.domain.JobSchedulerReservation
import org.springframework.data.jpa.repository.JpaRepository

interface JobSchedulerReservationCustomRepository {
    fun findDetailsById(id: Long): JobSchedulerReservation?

    fun findTopReservation(): JobSchedulerReservation?
}

interface JobSchedulerReservationRepository: JpaRepository<JobSchedulerReservation, Long>, JobSchedulerReservationCustomRepository