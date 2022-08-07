package cube8540.book.batch.translator.client

open class ClientExchangeException(message: String, cause: Throwable?): RuntimeException(message, cause) {
    constructor(message: String): this(message, null)
}

class ClientExchangeSystemException(message: String): ClientExchangeException(message)

class InvalidAuthenticationException(message: String): ClientExchangeException(message)

class InternalBadRequestException(message: String): ClientExchangeException(message)