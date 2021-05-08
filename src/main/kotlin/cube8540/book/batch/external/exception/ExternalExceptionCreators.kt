package cube8540.book.batch.external.exception

interface ErrorCodeExternalExceptionCreator {
    fun create(code: String, message: String): ExternalException
}

class DefaultErrorCodeExternalExceptionCreator: ErrorCodeExternalExceptionCreator {
    override fun create(code: String, message: String): ExternalException = ExternalException(message)
}