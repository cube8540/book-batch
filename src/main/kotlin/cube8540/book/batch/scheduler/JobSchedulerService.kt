package cube8540.book.batch.scheduler

import java.time.LocalDate

interface JobSchedulerService {

    fun launchBookDetailsRequest(from: LocalDate, to: LocalDate)

}