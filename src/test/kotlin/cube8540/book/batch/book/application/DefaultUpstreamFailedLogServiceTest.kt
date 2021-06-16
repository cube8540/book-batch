package cube8540.book.batch.book.application

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.UpstreamFailedLog
import cube8540.book.batch.book.domain.createFailedLog
import cube8540.book.batch.book.domain.createFailedReason
import cube8540.book.batch.book.repository.BookDetailsRepository
import cube8540.book.batch.book.repository.UpstreamFailedLogRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultUpstreamFailedLogServiceTest {

    private val upstreamFailedLogRepository: UpstreamFailedLogRepository = mockk(relaxed = true)
    private val bookDetailsRepository: BookDetailsRepository = mockk(relaxed = true)

    private val service = DefaultUpstreamFailedLogService(upstreamFailedLogRepository, bookDetailsRepository)

    @Test
    fun `register failed logs`() {
        val requests = listOf(
            createFailedLogRegisterRequest(isbn = "isbn0000", reasons = listOf(createFailedReason(property = "property0"))),
            createFailedLogRegisterRequest(isbn = "isbn0001", reasons = listOf(createFailedReason(property = "property1")))
        )
        val insertedLogCaptor = slot<List<UpstreamFailedLog>>()
        val bookDetails0: BookDetails = mockk()
        val bookDetails1: BookDetails = mockk()

        every { bookDetailsRepository.getOne("isbn0000") } returns bookDetails0
        every { bookDetailsRepository.getOne("isbn0001") } returns bookDetails1
        every { upstreamFailedLogRepository.saveAll(capture(insertedLogCaptor)) } returnsArgument 0

        service.registerFailedLogs(requests)
        assertThat(insertedLogCaptor.captured).containsExactly(
            createFailedLog(bookDetails0, listOf(createFailedReason(property = "property0"))),
            createFailedLog(bookDetails1, listOf(createFailedReason(property = "property1")))
        )
    }
}