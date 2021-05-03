package cube8540.book.batch.config

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIResponse
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.time.LocalDate

class NaverBookAPIObjectMapperTest {

    companion object {
        const val exampleXmlFile = "naver-book-api-response-exmaple.xml"
    }

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)
    private val xmlFile = File(javaClass.classLoader.getResource(exampleXmlFile)!!.file)

    private val objectMapper: ObjectMapper = ResponseMapperConfiguration()
        .naverLibraryObjectMapper(publisherRawMapper)

    @Test
    fun deserialization() {
        val xml = getXmlString()

        every { publisherRawMapper.mapping("<b>대원씨아이</b>") } returns "publishCode0001"
        every { publisherRawMapper.mapping("<b>대원씨아이</b>(만화/잡지)") } returns "publishCode0002"

        val result = objectMapper.readValue(xml, BookAPIResponse::class.java)
        assertThat(result.totalCount).isEqualTo(4)
        assertThat(result.page).isEqualTo(1)

        assertThat(result.books[0].isbn).isEqualTo("9791136226259")
        assertThat(result.books[0].title).isEqualTo("<b>학교생활</b> 12")
        assertThat(result.books[0].publisher).isEqualTo("publishCode0001")
        assertThat(result.books[0].publishDate).isEqualTo(LocalDate.of(2020, 3, 5))

        assertThat(result.books[1].isbn).isEqualTo("9791136202093")
        assertThat(result.books[1].title).isEqualTo("<b>학교생활</b> 11")
        assertThat(result.books[1].publisher).isEqualTo("publishCode0001")
        assertThat(result.books[1].publishDate).isEqualTo(LocalDate.of(2019, 7, 31))

        assertThat(result.books[2].isbn).isEqualTo("9791164120123")
        assertThat(result.books[2].title).isEqualTo("<b>학교생활</b>! 10")
        assertThat(result.books[2].publisher).isEqualTo("publishCode0001")
        assertThat(result.books[2].publishDate).isEqualTo(LocalDate.of(2019, 2, 28))

        assertThat(result.books[3].isbn).isEqualTo("8820024909354")
        assertThat(result.books[3].title).isEqualTo("<b>학교생활</b> 1~11 세트")
        assertThat(result.books[3].publisher).isEqualTo("publishCode0002")
        assertThat(result.books[3].publishDate).isEqualTo(LocalDate.of(2019, 7, 30))
    }

    private fun getXmlString(): String {
        val builder = StringBuilder()
        val reader = Files.newBufferedReader(xmlFile.toPath())

        reader.readLines().forEach { builder.append(it) }

        return builder.toString()
    }

}