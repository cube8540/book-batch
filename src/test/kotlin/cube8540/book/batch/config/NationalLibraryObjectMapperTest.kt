package cube8540.book.batch.config

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.external.BookAPIErrorResponse
import cube8540.book.batch.external.BookAPIResponse
import cube8540.book.batch.external.DefaultMapperConfiguration
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
        const val exampleErrorJsonFile = "national-library-response-error-example.json"
    }

    private val publisherRawMapper: PublisherRawMapper = mockk(relaxed = true)

    private val objectMapper: ObjectMapper = DefaultMapperConfiguration()
        .nationalLibraryObjectMapper(publisherRawMapper)

    @Test
    fun `book details deserialization`() {
        val json = getJsonString(exampleJsonFile)

        every { publisherRawMapper.mapping("대원씨아이(주)") } returns "publishCode0001"
        every { publisherRawMapper.mapping("대원씨아이") } returns "publishCode0002"

        val result = objectMapper.readValue(json, BookAPIResponse::class.java)
        assertThat(result.totalCount).isEqualTo(3)
        assertThat(result.page).isEqualTo(1)

        assertThat(result.books[0].resolveIsbn()).isEqualTo("9791136242808")
        assertThat(result.books[0].resolveTitle()).isEqualTo("학교생활! 1-12합본")
        assertThat(result.books[0].resolvePublisher()).isEqualTo("publishCode0001")
        assertThat(result.books[0].resolvePublishDate()).isEqualTo(LocalDate.of(2020, 8, 14))

        assertThat(result.books[1].resolveIsbn()).isEqualTo("9791136242792")
        assertThat(result.books[1].resolveTitle()).isEqualTo("학교생활! 12")
        assertThat(result.books[1].resolvePublisher()).isEqualTo("publishCode0001")
        assertThat(result.books[1].resolvePublishDate()).isEqualTo(LocalDate.of(2020, 8, 14))

        assertThat(result.books[2].resolveIsbn()).isEqualTo("9791136226259")
        assertThat(result.books[2].resolveTitle()).isEqualTo("학교생활! 12")
        assertThat(result.books[2].resolvePublisher()).isEqualTo("publishCode0002")
        assertThat(result.books[2].resolvePublishDate()).isEqualTo(LocalDate.of(2020, 4, 30))
    }

    @Test
    fun `error deserialization`() {
        val json = getJsonString(exampleErrorJsonFile)

        val result = objectMapper.readValue(json, BookAPIErrorResponse::class.java)
        assertThat(result.code).isEqualTo("010")
        assertThat(result.message).isEqualTo("인증키 정보가 없습니다.")
    }

    private fun getJsonString(filePath: String): String {
        val jsonFile = File(javaClass.classLoader.getResource(filePath)!!.file)
        val builder = StringBuilder()
        val reader = Files.newBufferedReader(jsonFile.toPath())

        reader.readLines().forEach { builder.append(it) }

        return builder.toString()
    }
}