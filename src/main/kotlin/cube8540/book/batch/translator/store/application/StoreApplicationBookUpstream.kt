package cube8540.book.batch.translator.store.application

import cube8540.book.batch.translator.BookUpstreamAPIRequest
import cube8540.book.batch.translator.ExternalBookAPIUpstream
import cube8540.book.batch.translator.store.client.StoreClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class StoreApplicationBookUpstream @Autowired constructor(
    private val storeClient: StoreClient,
    private val eventPublisher: ApplicationEventPublisher
): ExternalBookAPIUpstream {
    override fun upstream(upstreamRequest: BookUpstreamAPIRequest) {
        val result = storeClient.upstream(upstreamRequest)
        eventPublisher.publishEvent(StoreUpstreamCompletedEvent(result))
    }
}