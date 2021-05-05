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

class NationalLibraryObjectMapperTest {

    companion object {
        const val exampleJsonFile = "national-library-response-example.json"
    }

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)
    private val jsonFile = File(javaClass.classLoader.getResource(exampleJsonFile)!!.file)

    private val objectMapper: ObjectMapper = ResponseMapperConfiguration()
        .nationalLibraryObjectMapper(publisherRawMapper)

    @Test
    fun deserialization() {
        val json = getJsonString()

        every { publisherRawMapper.mapping("대원씨아이(주)") } returns "publishCode0001"
        every { publisherRawMapper.mapping("대원씨아이") } returns "publishCode0002"

        val result = objectMapper.readValue(json, BookAPIResponse::class.java)
        assertThat(result.totalCount).isEqualTo(3)
        assertThat(result.page).isEqualTo(1)

        assertThat(result.books[0].isbn).isEqualTo("9791136242808")
        assertThat(result.books[0].title).isEqualTo("학교생활! 1-12합본")
        assertThat(result.books[0].publisher).isEqualTo("publishCode0001")
        assertThat(result.books[0].publishDate).isEqualTo(LocalDate.of(2020, 8, 14))

        assertThat(result.books[1].isbn).isEqualTo("9791136242792")
        assertThat(result.books[1].title).isEqualTo("학교생활!")
        assertThat(result.books[1].publisher).isEqualTo("publishCode0001")
        assertThat(result.books[1].publishDate).isEqualTo(LocalDate.of(2020, 8, 14))

        assertThat(result.books[2].isbn).isEqualTo("9791136226259")
        assertThat(result.books[2].title).isEqualTo("학교생활!")
        assertThat(result.books[2].publisher).isEqualTo("publishCode0002")
        assertThat(result.books[2].publishDate).isEqualTo(LocalDate.of(2020, 4, 30))
    }

    private fun getJsonString(): String {
        val builder = StringBuilder()
        val reader = Files.newBufferedReader(jsonFile.toPath())

        reader.readLines().forEach { builder.append(it) }

        return builder.toString()
    }
}