package cube8540.book.batch.infra.nl.go

import cube8540.book.batch.external.exception.*

class NationalLibraryAPIErrorCodeExceptionCreator: ErrorCodeExternalExceptionCreator {
    override fun create(code: String, message: String): ExternalException = when (code) {
        "010" -> InvalidAuthenticationException(message)
        "011" -> InvalidAuthenticationException(message)
        "012" -> InternalBadRequestException(message)
        else -> ExternalSystemException(message)
    }
}