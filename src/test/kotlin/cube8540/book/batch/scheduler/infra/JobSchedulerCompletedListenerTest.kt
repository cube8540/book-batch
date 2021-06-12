package cube8540.book.batch.scheduler.infra

import cube8540.book.batch.scheduler.application.JobSchedulerLaunchParameter
import cube8540.book.batch.scheduler.application.JobSchedulerReservationService
import cube8540.book.batch.scheduler.domain.JobSchedulerFinishedEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.batch.core.JobExecution
import kotlin.random.Random

class JobSchedulerCompletedListenerTest {

    private val jobSchedulerReservationService: JobSchedulerReservationService = mockk(relaxed = true)

    private val listener = JobSchedulerFinishedListener(jobSchedulerReservationService)

    @Test
    fun `event consume when reservation id is null`() {
        val finishedEvent: JobSchedulerFinishedEvent = mockk(relaxed = true)
        val launchParameter: JobSchedulerLaunchParameter = mockk(relaxed = true)

        every { launchParameter.jobSchedulerReservationId } returns null
        every { finishedEvent.launchParameter } returns launchParameter

        listener.onApplicationEvent(finishedEvent)
        verify(exactly = 0) { jobSchedulerReservationService.addResult(any(), any()) }
    }

    @Test
    fun `event consume`() {
        val reservationId = Random.nextLong()
        val jobExecution: JobExecution = mockk(relaxed = true)
        val finishedEvent: JobSchedulerFinishedEvent = mockk(relaxed = true)
        val launchParameter: JobSchedulerLaunchParameter = mockk(relaxed = true)

        every { finishedEvent.jobExecution } returns jobExecution
        every { finishedEvent.launchParameter } returns launchParameter
        every { launchParameter.jobSchedulerReservationId } returns reservationId

        listener.onApplicationEvent(finishedEvent)
        verify { jobSchedulerReservationService.addResult(reservationId, jobExecution) }
    }
}