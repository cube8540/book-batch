package cube8540.book.batch.scheduler.application

interface JobSchedulerService {

    fun launchBookDetailsRequest(jobParameter: JobSchedulerLaunchParameter)

}