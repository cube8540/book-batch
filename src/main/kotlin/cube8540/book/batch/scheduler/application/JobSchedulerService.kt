package cube8540.book.batch.scheduler.application

import java.time.LocalDate

interface JobSchedulerService {

    fun launchBookDetailsRequest(from: LocalDate, to: LocalDate)

}