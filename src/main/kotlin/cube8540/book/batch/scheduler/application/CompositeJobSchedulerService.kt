package cube8540.book.batch.scheduler.application

import java.util.*

class CompositeJobSchedulerService: JobSchedulerService {

    private val delegates = LinkedList<JobSchedulerService>()

    override fun launchBookDetailsRequest(jobParameter: JobSchedulerLaunchParameter) {
        this.delegates.forEach { it.launchBookDetailsRequest(jobParameter) }
    }

    fun addDelegate(service: JobSchedulerService): CompositeJobSchedulerService {
        this.delegates.add(service)
        return this
    }

    fun clear() {
        this.delegates.clear()
    }
}