package cube8540.book.batch.translator.store.application

import cube8540.book.batch.translator.store.client.StoreUpstreamResponse
import org.springframework.context.ApplicationEvent

data class StoreUpstreamCompletedEvent(val result: StoreUpstreamResponse): ApplicationEvent(result)
