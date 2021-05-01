package cube8540.book.batch.domain

import cube8540.book.batch.domain.converter.ThumbnailConverter
import org.hibernate.annotations.CreationTimestamp
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "book_details")
class BookDetails(
    @Id
    @Column(name = "isbn", length = 13)
    var isbn: String
) {

    @Column(name = "title", length = 128, nullable = false)
    var title: String? = null

    @Column(name = "series_code", length = 32)
    var seriesCode: String? = null

    @ElementCollection
    @CollectionTable(name = "book_detail_divisions", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @Column(name = "division_code", length = 32)
    var divisions: Set<String>? = null

    @Column(name = "publisher_code", length = 32, nullable = false)
    var publisher: String? = null

    @Column(name = "publish_date", nullable = false)
    var publishDate: LocalDate? = null

    @ElementCollection
    @CollectionTable(name = "book_detail_authors", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @Column(name = "author", length = 32)
    var authors: Set<String>? = null

    @Convert(converter = ThumbnailConverter::class)
    @Column(name = "lage_thumbnail_url", length = 128)
    var largeThumbnail: URI? = null

    @Convert(converter = ThumbnailConverter::class)
    @Column(name = "medium_thumbnail_url", length = 128)
    var mediumThumbnail: URI? = null

    @Convert(converter = ThumbnailConverter::class)
    @Column(name = "small_thumbnail_url", length = 128)
    var smallThumbnail: URI? = null

    @Column(name = "description", length = 248)
    var description: String? = null

    @ElementCollection
    @CollectionTable(name = "book_detail_keywords", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @Column(name = "keyword", length = 32)
    var keywords: Set<String>? = null

    @Column(name = "price")
    var price: Double? = null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null

    @ElementCollection
    @CollectionTable(name = "book_detail_originals", joinColumns = [JoinColumn(name = "isbn", nullable = false)])
    @MapKeyClass(OriginalPropertyKey::class)
    @Column(name = "value", length = 128)
    var original: Map<OriginalPropertyKey, String?>? = null

    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        other is BookDetails && other.isbn == isbn -> true
        else -> false
    }

    override fun hashCode(): Int = isbn.hashCode()
}