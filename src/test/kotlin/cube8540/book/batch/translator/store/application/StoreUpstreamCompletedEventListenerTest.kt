package cube8540.book.batch.translator.store.application

import cube8540.book.batch.book.application.UpstreamFailedLogRegisterRequest
import cube8540.book.batch.book.application.UpstreamFailedLogService
import cube8540.book.batch.book.application.createFailedLogRegisterRequest
import cube8540.book.batch.book.domain.createFailedReason
import cube8540.book.batch.translator.store.client.StoreUpstreamFailedBooks
import cube8540.book.batch.translator.store.client.StoreUpstreamFailedReason
import cube8540.book.batch.translator.store.client.StoreUpstreamResponse
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class StoreUpstreamCompletedEventListenerTest {

    private val logService: UpstreamFailedLogService = mockk(relaxed = true)

    private val listener = UpstreamCompletedEventListener(logService)

    @Test
    fun `listing failed list is empty`() {
        val result = StoreUpstreamResponse(successBooks = emptyList(), failedBooks = emptyList())
        val event = StoreUpstreamCompletedEvent(result)

        listener.onApplicationEvent(event)
        verify { logService wasNot Called }
    }

    @Test
    fun `listing failed list is not empty`() {
        val failedBooks = listOf(
            StoreUpstreamFailedBooks(isbn = "isbn0000", errors = listOf(StoreUpstreamFailedReason("property0", "message0"))),
            StoreUpstreamFailedBooks(isbn = "isbn0001", errors = listOf(StoreUpstreamFailedReason("property1", "message1"))),
            StoreUpstreamFailedBooks(isbn = "isbn0002", errors = listOf(StoreUpstreamFailedReason("property2", "message2"))),
        )
        val result = StoreUpstreamResponse(successBooks = emptyList(), failedBooks)
        val event = StoreUpstreamCompletedEvent(result)

        val captor = slot<List<UpstreamFailedLogRegisterRequest>>()

        every { logService.registerFailedLogs(capture(captor)) } just Runs

        listener.onApplicationEvent(event)
        assertThat(captor.captured).containsExactly(
            createFailedLogRegisterRequest(isbn = "isbn0000", reasons = listOf(createFailedReason(property = "property0", message = "message0"))),
            createFailedLogRegisterRequest(isbn = "isbn0001", reasons = listOf(createFailedReason(property = "property1", message = "message1"))),
            createFailedLogRegisterRequest(isbn = "isbn0002", reasons = listOf(createFailedReason(property = "property2", message = "message2")))
        )
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }
}