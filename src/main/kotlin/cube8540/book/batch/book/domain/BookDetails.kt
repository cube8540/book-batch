package cube8540.book.batch.book.domain

import org.hibernate.annotations.BatchSize
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
    var isbn: String = context.extractIsbn()

    @Column(name = "title", length = 256, nullable = false)
    var title: String? = context.extractTitle()

    @Column(name = "series_code", length = 32)
    var seriesCode: String? = context.extractSeriesCode()

    @Column(name = "series_isbn", length = 32)
    var seriesIsbn: String? = context.extractSeriesIsbn()

    @ElementCollection
    @CollectionTable(name = "book_detail_divisions", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @Column(name = "division_code", length = 32)
    var divisions: MutableSet<String>? = context.extractDivisions()?.toMutableSet()

    @Column(name = "publisher_code", length = 32, nullable = false)
    var publisher: String? = context.extractPublisher()

    @Column(name = "publish_date", nullable = false)
    var publishDate: LocalDate? = context.extractPublishDate()

    @ElementCollection
    @CollectionTable(name = "book_detail_authors", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @Column(name = "author", length = 32)
    var authors: MutableSet<String>? = context.extractAuthors()?.toMutableSet()

    @Embedded
    var thumbnail: Thumbnail? = context.extractThumbnail()

    @Lob
    @Column(name = "description", columnDefinition = "text")
    var description: String? = context.extractDescription()

    @ElementCollection
    @CollectionTable(name = "book_indexes", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @Column(name = "title", length = 128)
    @OrderColumn(name = "odr")
    @BatchSize(size = 500)
    var indexes: MutableList<String>? = context.extractIndex()?.toMutableList()

    @ElementCollection
    @CollectionTable(name = "book_detail_keywords", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @Column(name = "keyword", length = 32)
    var keywords: MutableSet<String>? = context.extractKeywords()?.toMutableSet()

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = context.createdAt()

    @ElementCollection
    @CollectionTable(name = "book_detail_originals", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @MapKeyClass(OriginalPropertyKey::class)
    @Column(name = "value", length = 1024)
    var original: MutableMap<OriginalPropertyKey, String?>? = context.extractOriginal()?.toMutableMap()

    @ElementCollection
    @CollectionTable(name = "book_external_links", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @MapKeyColumn(name = "mapping_type", length = 32)
    @MapKeyEnumerated(EnumType.STRING)
    var externalLinks: MutableMap<MappingType, BookExternalLink>? = context.extractExternalLink()?.toMutableMap()

    @Column(name = "upstream_target", nullable = false)
    var isUpstreamTarget: Boolean? = false

    @Column(name = "confirmed_publication", nullable = false)
    var confirmedPublication: Boolean? = false

    @Transient
    var newObject: Boolean
        private set

    init {
        this.newObject = true
    }

    @PostLoad
    @PostPersist
    fun markingPersistedEntity() {
        this.newObject = false
    }

    fun markingConfirmedPublication() {
        this.confirmedPublication = true
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