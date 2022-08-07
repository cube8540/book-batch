package cube8540.book.batch.translator.naver.com.client

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.translator.client.ClientExchangeException
import cube8540.book.batch.translator.client.InternalBadRequestException
import cube8540.book.batch.translator.client.InvalidAuthenticationException
import feign.Response
import feign.codec.ErrorDecoder
import java.io.IOException
import java.nio.charset.StandardCharsets

class NaverBookClientErrorDecoder constructor(private val objectMapper: ObjectMapper): ErrorDecoder {

    override fun decode(methodKey: String?, response: Response?): Exception {
        return try {
            convertErrorCodeToException(deserialize(response))
        } catch (e: IOException) {
            ClientExchangeException("cannot deserializing response to NaverClientError", e)
        }
    }

    private fun deserialize(response: Response?): NaverBookClientError {
        val errorReader = response?.body()?.asReader(StandardCharsets.UTF_8)
            ?: throw ClientExchangeException("unknown exception response body is empty")

        return objectMapper.readValue(errorReader, NaverBookClientError::class.java)
    }

    private fun convertErrorCodeToException(error: NaverBookClientError) = when (error.errorCode) {
        "SE01" -> InternalBadRequestException(error.errorMessage)
        "SE02" -> InternalBadRequestException(error.errorMessage)
        "SE03" -> InternalBadRequestException(error.errorMessage)
        "SE04" -> InternalBadRequestException(error.errorMessage)
        "SE05" -> InternalBadRequestException(error.errorMessage)
        "SE06" -> InternalBadRequestException(error.errorMessage)
        else -> InvalidAuthenticationException(error.errorMessage)
    }
}