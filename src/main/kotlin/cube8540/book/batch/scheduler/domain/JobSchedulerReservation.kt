package cube8540.book.batch.scheduler.domain

import org.hibernate.annotations.DynamicUpdate
import org.springframework.batch.core.JobExecution
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "job_scheduler_reservations")
@DynamicUpdate
class JobSchedulerReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "name", length = 32)
    var name: String? = null

    @Column(name = "from")
    var from: LocalDate? = null

    @Column(name = "to")
    var to: LocalDate? = null

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: JobSchedulerReservationStatus? = null

    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "job_scheduler_results", joinColumns = [JoinColumn(name = "reservation_id")])
    @Column(name = "job_instance_id")
    var results: MutableList<Long>? = null

    fun addResult(jobExecution: JobExecution) {
        if (results == null) {
            results = ArrayList()
        }
        results?.add(jobExecution.jobId)
    }

    override fun equals(other: Any?): Boolean = when (other) {
        null -> false
        else -> other is JobSchedulerReservation && other.id == id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}