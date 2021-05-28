package cube8540.book.batch.scheduler.domain

import cube8540.book.batch.scheduler.application.JobSchedulerLaunchParameter
import org.springframework.batch.core.JobExecution
import org.springframework.context.ApplicationEvent

data class JobSchedulerFinishedEvent(
    val jobExecution: JobExecution,
    val launchParameter: JobSchedulerLaunchParameter
): ApplicationEvent(jobExecution)