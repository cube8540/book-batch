package cube8540.book.batch.external.nl.go

import cube8540.book.batch.interlock.client.*

class NationalLibraryAPIErrorCodeExceptionCreator: ErrorCodeExternalExceptionCreator {
    override fun create(code: String, message: String): ClientExchangeException = when (code) {
        "010" -> InvalidAuthenticationException(message)
        "011" -> InvalidAuthenticationException(message)
        "012" -> InternalBadRequestException(message)
        else -> ClientExchangeSystemException(message)
    }
}