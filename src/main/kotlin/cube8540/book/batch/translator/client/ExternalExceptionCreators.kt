package cube8540.book.batch.translator.client

interface ErrorCodeExternalExceptionCreator {
    fun create(code: String, message: String): ClientExchangeException
}

class DefaultErrorCodeExternalExceptionCreator: ErrorCodeExternalExceptionCreator {
    override fun create(code: String, message: String): ClientExchangeException = ClientExchangeException(message)
}