package cube8540.book.batch.translator.store.application

import cube8540.book.batch.book.application.UpstreamFailedLogRegisterRequest
import cube8540.book.batch.book.application.UpstreamFailedLogService
import cube8540.book.batch.book.domain.UpstreamFailedReason
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class UpstreamCompletedEventListener @Autowired constructor(private val logService: UpstreamFailedLogService)
    : ApplicationListener<StoreUpstreamCompletedEvent> {

    override fun onApplicationEvent(event: StoreUpstreamCompletedEvent) {
        event.result.failedBooks
            ?.map { UpstreamFailedLogRegisterRequest(it.isbn, it.errors.map { err -> UpstreamFailedReason(err.property, err.message) }) }
            ?.let {
                if (it.isNotEmpty()) {
                    logService.registerFailedLogs(it)
                }
            }
    }
}