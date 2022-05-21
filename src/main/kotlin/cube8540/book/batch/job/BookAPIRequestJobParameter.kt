package cube8540.book.batch.job

import cube8540.book.batch.interlock.BookAPIRequest
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JobScope
@Component
class BookAPIRequestJobParameter {

    @set:Value("#{jobParameters[isbn]}")
    var isbn: String? = null

    @set:Value("#{jobParameters[publisher]}")
    var publisher: String? = null

    var startup: LocalDateTime? = null

    var from: LocalDate? = null

    var to: LocalDate? = null

    @Value("#{jobParameters[from]}")
    fun setFrom(value: String?) {
        this.from = value?.let { LocalDate.parse(it, DateTimeFormatter.BASIC_ISO_DATE) }
    }

    @Value("#{jobParameters[to]}")
    fun setTo(value: String?) {
        this.to = value?.let { LocalDate.parse(it, DateTimeFormatter.BASIC_ISO_DATE) }
    }

    @Value("#{jobParameters[startup]}")
    fun setStartup(value: String?) {
        this.startup = value?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) }
    }

    fun toRequest(page: Int, pageSize: Int): BookAPIRequest = BookAPIRequest(page = page, size = pageSize, from = from, to = to, isbn = isbn, publisher = publisher)
}