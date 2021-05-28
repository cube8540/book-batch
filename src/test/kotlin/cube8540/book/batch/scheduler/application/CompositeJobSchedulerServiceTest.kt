package cube8540.book.batch.scheduler.application

import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CompositeJobSchedulerServiceTest {

    private val from = LocalDate.of(2021, 1, 1)
    private val to = LocalDate.of(2021, 5, 31)

    private val service = CompositeJobSchedulerService()

    @Test
    fun `run all delegates`() {
        val launchParameter = JobSchedulerLaunchParameter(from, to)
        val delegator0: JobSchedulerService = mockk(relaxed = true)
        val delegator1: JobSchedulerService = mockk(relaxed = true)
        val delegator2: JobSchedulerService = mockk(relaxed = true)

        service.addDelegate(delegator0)
            .addDelegate(delegator1)
            .addDelegate(delegator2)

        service.launchBookDetailsRequest(launchParameter)
        verifyOrder {
            delegator0.launchBookDetailsRequest(launchParameter)
            delegator1.launchBookDetailsRequest(launchParameter)
            delegator2.launchBookDetailsRequest(launchParameter)
        }
    }

    @AfterEach
    fun cleanup() {
        this.service.clear()
    }

}