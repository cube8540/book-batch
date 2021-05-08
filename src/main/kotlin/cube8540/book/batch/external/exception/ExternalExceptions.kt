package cube8540.book.batch.external.exception

open class ExternalException(message: String): RuntimeException(message)

class ExternalSystemException(message: String): ExternalException(message)

class InvalidAuthenticationException(message: String): ExternalException(message)

class InternalBadRequestException(message: String): ExternalException(message)