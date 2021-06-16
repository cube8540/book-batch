package cube8540.book.batch.book.application

import cube8540.book.batch.book.domain.UpstreamFailedReason
import cube8540.book.batch.book.domain.defaultIsbn

fun createFailedLogRegisterRequest(
    isbn: String = defaultIsbn,
    reasons: List<UpstreamFailedReason> = emptyList()
) = UpstreamFailedLogRegisterRequest(isbn, reasons)