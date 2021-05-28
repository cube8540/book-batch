package cube8540.book.batch.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.domain.Persistable
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "book_details")
@DynamicInsert
@DynamicUpdate
class BookDetails(context: BookDetailsContext): Persistable<String> {

    @Id
    @Column(name = "isbn", length = 13)
    var isbn: String = context.resolveIsbn()

    @Column(name = "title", length = 256, nullable = false)
    var title: String? = context.resolveTitle()

    @Column(name = "series_code", length = 32)
    var seriesCode: String? = context.resolveSeriesCode()

    @Column(name = "series_isbn", length = 32)
    var seriesIsbn: String? = context.resolveSeriesIsbn()

    @ElementCollection
    @CollectionTable(name = "book_detail_divisions", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @Column(name = "division_code", length = 32)
    var divisions: MutableSet<String>? = context.resolveDivisions()?.toMutableSet()

    @Column(name = "publisher_code", length = 32, nullable = false)
    var publisher: String? = context.resolvePublisher()

    @Column(name = "publish_date", nullable = false)
    var publishDate: LocalDate? = context.resolvePublishDate()

    @ElementCollection
    @CollectionTable(name = "book_detail_authors", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @Column(name = "author", length = 32)
    var authors: MutableSet<String>? = context.resolveAuthors()?.toMutableSet()

    @Embedded
    var thumbnail: Thumbnail? = context.resolveThumbnail()

    @Lob
    @Column(name = "description", columnDefinition = "text")
    var description: String? = context.resolveDescription()

    @ElementCollection
    @CollectionTable(name = "book_detail_keywords", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @Column(name = "keyword", length = 32)
    var keywords: MutableSet<String>? = context.resolveKeywords()?.toMutableSet()

    @Column(name = "price")
    var price: Double? = context.resolvePrice()

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = context.createdAt()

    @ElementCollection
    @CollectionTable(name = "book_detail_originals", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @MapKeyClass(OriginalPropertyKey::class)
    @Column(name = "value", length = 1024)
    var original: MutableMap<OriginalPropertyKey, String?>? = context.resolveOriginal()?.toMutableMap()

    @Column(name = "upstream_target", nullable = false)
    var isUpstreamTarget: Boolean? = false

    @Transient
    var newObject: Boolean
        private set

    init {
        this.newObject = true
    }

    @PostLoad
    fun markingPersistedEntity() {
        this.newObject = false
    }

    override fun getId(): String = isbn

    override fun isNew(): Boolean = newObject

    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        other is BookDetails && other.isbn == isbn -> true
        else -> false
    }

    override fun hashCode(): Int = isbn.hashCode()
}