package cube8540.book.batch.scheduler.application

import cube8540.book.batch.scheduler.domain.JobSchedulerReservation
import cube8540.book.batch.scheduler.domain.JobSchedulerReservationStatus
import cube8540.book.batch.scheduler.domain.repository.JobSchedulerReservationRepository
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.batch.core.JobExecution
import kotlin.random.Random

class DefaultJobSchedulerReservationServiceTest {

    private val reservationId: Long = Random.nextLong()
    private val status: JobSchedulerReservationStatus = JobSchedulerReservationStatus
        .values()[Random.nextInt(0, JobSchedulerReservationStatus.values().size)]

    private val repository: JobSchedulerReservationRepository = mockk(relaxed = true)

    private val service = DefaultJobSchedulerReservationService(repository)

    @Test
    fun `add result when reservation is null`() {
        val jobExecution: JobExecution = mockk(relaxed = true)

        every { repository.findDetailsById(reservationId) } returns null
        every { repository.save(any()) } returnsArgument 0

        service.addResult(reservationId, jobExecution)
        verify(exactly = 0) {
            repository.save(any())
        }
    }

    @Test
    fun `add result`() {
        val reservation: JobSchedulerReservation = mockk(relaxed = true)
        val jobExecution: JobExecution = mockk(relaxed = true)

        every { repository.findDetailsById(reservationId) } returns reservation
        every { repository.save(any()) } returnsArgument 0

        service.addResult(reservationId, jobExecution)
        verifyOrder {
            reservation.addResult(jobExecution)
            repository.save(reservation)
        }
    }

    @Test
    fun `update status when returns null`() {
        every { repository.findDetailsById(reservationId) } returns null
        every { repository.save(any()) } returnsArgument 0

        service.updateStatus(reservationId, status)
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `update status to given status`() {
        val reservation: JobSchedulerReservation = mockk(relaxed = true)

        every { repository.findDetailsById(reservationId) } returns reservation
        every { repository.save(any()) } returnsArgument 0

        service.updateStatus(reservationId, status)
        verifyOrder {
            reservation.status = status
            repository.save(reservation)
        }
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }
}