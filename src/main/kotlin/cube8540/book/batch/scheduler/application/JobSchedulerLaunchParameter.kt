package cube8540.book.batch.scheduler.application

import java.time.LocalDate

data class JobSchedulerLaunchParameter(
    val from: LocalDate,
    val to: LocalDate,
    val jobSchedulerReservationId: Long?
) {
    constructor(from: LocalDate, to: LocalDate): this(from, to, null)
}