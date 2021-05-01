package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.MappingType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Date
import java.sql.Timestamp
import java.sql.Types
import javax.sql.DataSource

@Repository
class BookDetailsPersistCustomRepositoryImpl(dataSource: DataSource): BookDetailsPersistCustomRepository {

    companion object {
        private const val insertBookDetails = "insert into book_details (isbn, title, series_code, publisher_code, publish_date, lage_thumbnail_url, medium_thumbnail_url, small_thumbnail_url, description, price, created_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        private const val insertBookDetailDivisions = "insert into book_detail_divisions (isbn, division_code) values (?, ?)"
        private const val insertBookDetailAuthors = "insert into book_detail_authors (isbn, author) values (?, ?)"
        private const val insertBookDetailKeywords = "insert into book_detail_keywords (isbn, keyword) values (?, ?)"
        private const val insertBookDetailOriginals = "insert into book_detail_originals (isbn, property, mapping_type, value) values (?, ?, ?, ?)"
        private const val defaultBatchSize: Int = 500
    }

    val jdbcTemplate: JdbcTemplate = JdbcTemplate(dataSource)

    var batchSize: Int = defaultBatchSize

    override fun persistBookDetails(bookDetails: Collection<BookDetails>) {
        this.jdbcTemplate.batchUpdate(insertBookDetails, bookDetails, batchSize) { ps, arguments ->
            ps.setString(1, arguments.isbn)
            ps.setString(2, arguments.title)
            ps.setString(3, arguments.seriesCode)
            ps.setString(4, arguments.publisher)
            ps.setDate(5, arguments.publishDate?.let { it -> Date.valueOf(it) })
            ps.setString(6, arguments.largeThumbnail?.toString())
            ps.setString(7, arguments.mediumThumbnail?.toString())
            ps.setString(8, arguments.smallThumbnail?.toString())
            ps.setString(9, arguments.description)
            arguments.price?.let { it -> ps.setDouble(10, it) } ?: run { ps.setNull(10, Types.NULL) }
            ps.setTimestamp(11, arguments.createdAt?.let { it -> Timestamp.valueOf(it) })
        }
    }

    override fun persistDivision(bookDetails: Collection<BookDetails>) {
        val properties = ArrayList<BookProperty>()
        bookDetails.forEach { book ->
            book.divisions?.forEach { division -> properties.add(BookProperty(book.isbn, division)) }
        }
        propertyBatchUpdate(insertBookDetailDivisions, properties)
    }

    override fun persistAuthors(bookDetails: Collection<BookDetails>) {
        val properties = ArrayList<BookProperty>()
        bookDetails.forEach { book ->
            book.authors?.forEach { author -> properties.add(BookProperty(book.isbn, author)) }
        }
        propertyBatchUpdate(insertBookDetailAuthors, properties)
    }

    override fun persistKeywords(bookDetails: Collection<BookDetails>) {
        val properties = ArrayList<BookProperty>()
        bookDetails.forEach { book ->
            book.keywords?.forEach { keyword -> properties.add(BookProperty(book.isbn, keyword)) }
        }
        propertyBatchUpdate(insertBookDetailKeywords, properties)
    }

    override fun persistOriginals(bookDetails: Collection<BookDetails>) {
        val properties = ArrayList<BookOriginalProperty>()
        bookDetails.forEach { book ->
            book.original?.forEach { entry -> properties.add(BookOriginalProperty(book.isbn, entry.key.property, entry.key.mappingType, entry.value)) }
        }
        if (properties.isNotEmpty()) {
            this.jdbcTemplate.batchUpdate(insertBookDetailOriginals, properties, batchSize) { ps, arguments ->
                ps.setString(1, arguments.isbn)
                ps.setString(2, arguments.property)
                ps.setString(3, arguments.mappingType.toString())
                ps.setString(4, arguments.value)
            }
        }
    }

    private fun propertyBatchUpdate(sql: String, properties: Collection<BookProperty>) {
        if (properties.isNotEmpty()) {
            this.jdbcTemplate.batchUpdate(sql, properties, batchSize) { ps, arguments ->
                ps.setString(1, arguments.isbn)
                ps.setString(2, arguments.value)
            }
        }
    }

    private data class BookProperty(val isbn: String, val value: String)

    private data class BookOriginalProperty(val isbn: String, val property: String, val mappingType: MappingType, val value: String?)
}
