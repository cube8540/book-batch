package cube8540.book.batch.scheduler.domain

import org.springframework.batch.core.JobExecution
import org.springframework.context.ApplicationEvent

data class JobSchedulerFinishedEvent(private val jobExecution: JobExecution): ApplicationEvent(jobExecution)