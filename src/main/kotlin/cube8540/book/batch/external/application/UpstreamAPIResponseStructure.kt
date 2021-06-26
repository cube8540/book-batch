package cube8540.book.batch.external.application

data class ExternalUpstreamResponse(
    var successBooks: List<String>?,
    var failedBooks: List<ExternalUpstreamFailedBooks>?
)

data class ExternalUpstreamFailedBooks(
    var isbn: String,
    var errors: List<ExternalUpstreamFailedReason>
)

data class ExternalUpstreamFailedReason(
    var property: String,
    var message: String
)