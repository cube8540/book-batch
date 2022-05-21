package cube8540.book.batch.interlock.naver.com.client

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.interlock.client.InternalBadRequestException
import cube8540.book.batch.interlock.client.InvalidAuthenticationException
import feign.Response
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.StringReader
import java.nio.charset.StandardCharsets

internal class NaverBookClientErrorDecoderTest {

    private val objectMapper = ObjectMapper()
    private val errorDecoder = NaverBookClientErrorDecoder(objectMapper)

    @Test
    fun `error deserialize when code is SE01`() {
        val errorJson = createErrorString(errorCode = "SE01")

        val response: Response = createFeignResponse(errorJson)

        val deserialize = errorDecoder.decode("#", response)
        assertThat(deserialize).isInstanceOf(InternalBadRequestException::class.java)
    }

    @Test
    fun `error deserialize when code is SE02`() {
        val errorJson = createErrorString(errorCode = "SE02")
        val response: Response = createFeignResponse(errorJson)

        val deserialize = errorDecoder.decode("#", response)
        assertThat(deserialize).isInstanceOf(InternalBadRequestException::class.java)
    }

    @Test
    fun `error deserialize when code is SE03`() {
        val errorJson = createErrorString(errorCode = "SE03")
        val response: Response = createFeignResponse(errorJson)

        val deserialize = errorDecoder.decode("#", response)
        assertThat(deserialize).isInstanceOf(InternalBadRequestException::class.java)
    }

    @Test
    fun `error deserialize when code is SE04`() {
        val errorJson = createErrorString(errorCode = "SE04")
        val response: Response = createFeignResponse(errorJson)

        val deserialize = errorDecoder.decode("#", response)
        assertThat(deserialize).isInstanceOf(InternalBadRequestException::class.java)
    }

    @Test
    fun `error deserialize when code is SE05`() {
        val errorJson = createErrorString(errorCode = "SE05")
        val response: Response = createFeignResponse(errorJson)

        val deserialize = errorDecoder.decode("#", response)
        assertThat(deserialize).isInstanceOf(InternalBadRequestException::class.java)
    }

    @Test
    fun `error deserialize when code is SE06`() {
        val errorJson = createErrorString(errorCode = "SE06")
        val response: Response = createFeignResponse(errorJson)

        val deserialize = errorDecoder.decode("#", response)
        assertThat(deserialize).isInstanceOf(InternalBadRequestException::class.java)
    }

    @Test
    fun `error deserialize when code is 024`() {
        val errorJson = createErrorString(errorCode = "024")
        val response: Response = createFeignResponse(errorJson)

        val deserialize = errorDecoder.decode("#", response)
        assertThat(deserialize).isInstanceOf(InvalidAuthenticationException::class.java)
    }

    private fun createFeignResponse(errorJson: String): Response {
        val reader = StringReader(errorJson)
        val responseBody: Response.Body = mockk {
            every { asReader(StandardCharsets.UTF_8) } returns reader
        }
        val response: Response = mockk {
            every { body() } returns responseBody
        }
        return response
    }
}