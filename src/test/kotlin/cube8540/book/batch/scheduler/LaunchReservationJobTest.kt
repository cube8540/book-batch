package cube8540.book.batch.scheduler

import cube8540.book.batch.scheduler.application.JobSchedulerLaunchParameter
import cube8540.book.batch.scheduler.application.JobSchedulerReservationDetails
import cube8540.book.batch.scheduler.application.JobSchedulerReservationService
import cube8540.book.batch.scheduler.application.JobSchedulerService
import cube8540.book.batch.scheduler.domain.JobSchedulerReservationStatus
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.random.Random

class LaunchReservationJobTest {

    private val regularJobSchedulerService: JobSchedulerService = mockk(relaxed = true)
    private val nonRegularJobSchedulerService: JobSchedulerService = mockk(relaxed = true)
    private val jobSchedulerReservationService: JobSchedulerReservationService = mockk(relaxed = true)

    private val from = LocalDate.of(2021, 1, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val jobScheduler = JobSchedulerConfiguration()

    init {
        jobScheduler.regularJobSchedulerService = regularJobSchedulerService
        jobScheduler.nonRegularJobSchedulerService = nonRegularJobSchedulerService
        jobScheduler.jobSchedulerReservationService = jobSchedulerReservationService
    }

    @Test
    fun `job launch when reservation is null`() {
        every { jobSchedulerReservationService.getReservation() } returns null

        jobScheduler.launchReservationJob()
        verify(exactly = 0) {
            jobSchedulerReservationService.updateStatus(any(), any())
            regularJobSchedulerService.launchBookDetailsRequest(any())
            nonRegularJobSchedulerService.launchBookDetailsRequest(any())
        }
    }

    @Test
    fun `reservation job launch`() {
        val reservationId = Random.nextLong()
        val reservation: JobSchedulerReservationDetails = mockk(relaxed = true)

        every { jobSchedulerReservationService.getReservation() } returns reservation

        every { reservation.reservationId } returns reservationId
        every { reservation.from } returns from
        every { reservation.to } returns to

        val parameter = JobSchedulerLaunchParameter(from, to, reservationId)
        jobScheduler.launchReservationJob()
        verify(exactly = 0) {
            regularJobSchedulerService.launchBookDetailsRequest(any())
        }
        verifyOrder {
            jobSchedulerReservationService.updateStatus(reservationId, JobSchedulerReservationStatus.PROCESSING)
            nonRegularJobSchedulerService.launchBookDetailsRequest(parameter)
            jobSchedulerReservationService.updateStatus(reservationId, JobSchedulerReservationStatus.COMPLETED)
        }
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

}