package cube8540.book.batch.translator.nl.go.client

import cube8540.book.batch.translator.client.*

class NationalLibraryAPIErrorCodeExceptionCreator: ErrorCodeExternalExceptionCreator {
    override fun create(code: String, message: String): ClientExchangeException = when (code) {
        "010" -> InvalidAuthenticationException(message)
        "011" -> InvalidAuthenticationException(message)
        "012" -> InternalBadRequestException(message)
        else -> ClientExchangeSystemException(message)
    }
}