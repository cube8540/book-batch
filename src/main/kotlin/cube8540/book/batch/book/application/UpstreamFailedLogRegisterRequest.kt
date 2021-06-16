package cube8540.book.batch.book.application

import cube8540.book.batch.book.domain.UpstreamFailedReason

data class UpstreamFailedLogRegisterRequest(
    var isbn: String,
    var reasons: List<UpstreamFailedReason>
)
