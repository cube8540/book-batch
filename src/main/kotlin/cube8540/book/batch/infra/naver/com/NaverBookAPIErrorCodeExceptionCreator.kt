package cube8540.book.batch.infra.naver.com

import cube8540.book.batch.external.exception.ErrorCodeExternalExceptionCreator
import cube8540.book.batch.external.exception.ExternalException
import cube8540.book.batch.external.exception.InternalBadRequestException
import cube8540.book.batch.external.exception.InvalidAuthenticationException

class NaverBookAPIErrorCodeExceptionCreator: ErrorCodeExternalExceptionCreator {
    override fun create(code: String, message: String): ExternalException = when (code) {
        "SE01" -> InternalBadRequestException(message)
        "SE02" -> InternalBadRequestException(message)
        "SE03" -> InternalBadRequestException(message)
        "SE04" -> InternalBadRequestException(message)
        "SE05" -> InternalBadRequestException(message)
        "SE06" -> InternalBadRequestException(message)
        "024" -> InvalidAuthenticationException(message)
        else -> ExternalException(message)
    }
}