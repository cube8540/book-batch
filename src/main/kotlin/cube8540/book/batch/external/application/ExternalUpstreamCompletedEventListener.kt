package cube8540.book.batch.external.application

import cube8540.book.batch.book.application.UpstreamFailedLogRegisterRequest
import cube8540.book.batch.book.application.UpstreamFailedLogService
import cube8540.book.batch.book.domain.UpstreamFailedReason
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class ExternalUpstreamCompletedEventListener
@Autowired
constructor(private val logService: UpstreamFailedLogService): ApplicationListener<ExternalUpstreamCompletedEvent> {

    override fun onApplicationEvent(event: ExternalUpstreamCompletedEvent) {
        if (event.result.failedBooks.isNotEmpty()) {
            val requests = event.result.failedBooks.map { failed ->
                UpstreamFailedLogRegisterRequest(failed.isbn, failed.errors.map { error ->
                    UpstreamFailedReason(error.property, error.message)
                })
            }

            logService.registerFailedLogs(requests)
        }
    }
}