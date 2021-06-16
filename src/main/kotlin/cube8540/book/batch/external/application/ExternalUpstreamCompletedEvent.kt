package cube8540.book.batch.external.application

import org.springframework.context.ApplicationEvent

data class ExternalUpstreamCompletedEvent(val result: ExternalUpstreamResponse): ApplicationEvent(result)
