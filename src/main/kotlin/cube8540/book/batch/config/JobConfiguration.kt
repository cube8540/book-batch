package cube8540.book.batch.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class JobConfiguration {

    @set:Autowired
    lateinit var jobTaskExecutorProperty: JobTaskExecutorProperty

    @Bean
    fun jobTaskExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = jobTaskExecutorProperty.corePoolSize!!
        executor.maxPoolSize = jobTaskExecutorProperty.maxPoolSize!!
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.initialize()
        return executor
    }
}