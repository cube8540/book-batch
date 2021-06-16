package cube8540.book.batch.book.domain

import cube8540.book.batch.BatchApplication
import org.springframework.data.domain.Persistable
import java.time.Clock
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "book_upstream_failed_logs")
class UpstreamFailedLog(book: BookDetails, reasons: List<UpstreamFailedReason>): Persistable<Long> {

    companion object {
        internal var clock = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sequence")
    var sequence: Long? = null
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", nullable = false)
    var book: BookDetails = book

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_upstream_failed_reasons", joinColumns = [JoinColumn(name = "failed_id", nullable = false)])
    @AttributeOverrides(value = [
        AttributeOverride(name = "property", column = Column(name = "property", length = 64, nullable = false)),
        AttributeOverride(name = "message", column = Column(name = "message", length = 128, nullable = false))
    ])
    var reasons: MutableList<UpstreamFailedReason> = reasons.toMutableList()

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(clock)

    @Transient
    var newObject: Boolean = true
        private set

    @PostLoad
    @PostPersist
    fun markingPersistedEntity() {
        this.newObject = false
    }

    override fun getId(): Long? = sequence

    override fun isNew(): Boolean = newObject

    override fun equals(other: Any?): Boolean = when (other) {
        null -> false
        is UpstreamFailedLog -> other.id == this.id
        else -> false
    }

    override fun hashCode(): Int = id?.toInt() ?: 0
}