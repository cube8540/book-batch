package cube8540.book.batch.scheduler.application

import cube8540.book.batch.scheduler.domain.JobSchedulerReservation
import java.time.LocalDate
import java.time.LocalDateTime

data class JobSchedulerReservationDetails(
    val reservationId: Long,
    val name: String?,
    val from: LocalDate,
    val to: LocalDate,
    val createdAt: LocalDateTime,
    val results: List<Long>?
) {
    companion object {
        fun of(reservation: JobSchedulerReservation): JobSchedulerReservationDetails =
            JobSchedulerReservationDetails(
                reservationId = reservation.id!!,
                name = reservation.name,
                from = reservation.from!!,
                to = reservation.to!!,
                createdAt = reservation.createdAt!!,
                results = reservation.results
            )
    }
}