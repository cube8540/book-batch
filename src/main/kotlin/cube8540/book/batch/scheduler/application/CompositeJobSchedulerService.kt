package cube8540.book.batch.scheduler.application

import java.time.LocalDate
import java.util.*

class CompositeJobSchedulerService: JobSchedulerService {

    private val delegates = LinkedList<JobSchedulerService>()

    override fun launchBookDetailsRequest(from: LocalDate, to: LocalDate) {
        this.delegates.forEach { it.launchBookDetailsRequest(from, to) }
    }

    fun addDelegate(service: JobSchedulerService): CompositeJobSchedulerService {
        this.delegates.add(service)
        return this
    }

    fun clear() {
        this.delegates.clear()
    }
}