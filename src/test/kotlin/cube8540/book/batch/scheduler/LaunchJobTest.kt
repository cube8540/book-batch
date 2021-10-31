package cube8540.book.batch.scheduler

import cube8540.book.batch.BatchApplication
import cube8540.book.batch.scheduler.application.JobSchedulerLaunchParameter
import cube8540.book.batch.scheduler.application.JobSchedulerService
import cube8540.book.batch.toDefaultInstance
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

class LaunchJobTest {
    private val jobSchedulerService: JobSchedulerService = mockk(relaxed = true)
    private val from = LocalDate.of(2021, 1, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val datetime = LocalDateTime.of(2021, 5, 29, 3, 26, 0)

    private val jobScheduler = JobSchedulerConfiguration()

    init {
        jobScheduler.regularJobSchedulerService = jobSchedulerService
        JobSchedulerConfiguration.clock = Clock.fixed(datetime.toDefaultInstance(), BatchApplication.DEFAULT_TIME_ZONE.toZoneId())
    }

    @Test
    fun `launch job`() {
        jobScheduler.launchJob()

        verify {
            jobSchedulerService.launchBookDetailsRequest(JobSchedulerLaunchParameter(
                datetime.toLocalDate().minusMonths(3),
                datetime.toLocalDate().plusMonths(3)
            ))
        }
    }
}