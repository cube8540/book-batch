package cube8540.book.batch.scheduler.domain.repository

import cube8540.book.batch.scheduler.domain.JobSchedulerReservation
import cube8540.book.batch.scheduler.domain.JobSchedulerReservationStatus
import cube8540.book.batch.scheduler.domain.QJobSchedulerReservation
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class JobSchedulerReservationCustomRepositoryImpl: JobSchedulerReservationCustomRepository, QuerydslRepositorySupport(JobSchedulerReservation::class.java) {

    private val jobSchedulerReservation = QJobSchedulerReservation.jobSchedulerReservation

    override fun findDetailsById(id: Long): JobSchedulerReservation? =
        from(jobSchedulerReservation)
            .leftJoin(jobSchedulerReservation.results).fetchJoin()
            .where(jobSchedulerReservation.id.eq(id))
            .fetchOne()

    override fun findTopReservation(): JobSchedulerReservation? {
        val queryResults = from(jobSchedulerReservation)
            .where(jobSchedulerReservation.status.eq(JobSchedulerReservationStatus.WAITING))
            .orderBy(jobSchedulerReservation.createdAt.asc())
            .limit(1)
            .fetch()
        return if (queryResults.isEmpty()) {
            null
        } else {
            queryResults[0]
        }
    }
}