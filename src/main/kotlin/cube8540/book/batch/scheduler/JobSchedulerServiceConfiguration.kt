package cube8540.book.batch.scheduler

import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.repository.PublisherRepository
import cube8540.book.batch.job.*
import cube8540.book.batch.scheduler.application.CompositeJobSchedulerService
import cube8540.book.batch.scheduler.application.JobSchedulerService
import cube8540.book.batch.scheduler.application.LocalDateJobSchedulerService
import cube8540.book.batch.scheduler.application.LocalDateWithPublisherSchedulerService
import org.springframework.batch.core.Job
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JobSchedulerServiceConfiguration {

    @set:Autowired
    lateinit var applicationContext: ApplicationContext

    @set:Autowired
    lateinit var jobLauncher: JobLauncher

    @set:Autowired
    lateinit var publisherRepository: PublisherRepository

    @Bean
    fun jobSchedulerService(): JobSchedulerService {
        val nationalLibraryAPIJobSchedulerService = LocalDateWithPublisherSchedulerService(MappingType.NATIONAL_LIBRARY, publisherRepository)
        val naverBookAPIJobSchedulerService = LocalDateWithPublisherSchedulerService(MappingType.NAVER_BOOK, publisherRepository)

        val kyoboBookRequestJobSchedulerService = LocalDateJobSchedulerService(
            applicationContext.getBean(KyoboBookRequestJobConfiguration.jobName, Job::class.java), jobLauncher)
        val setUpstreamJobSchedulerService = LocalDateJobSchedulerService(
            applicationContext.getBean(BookSetUpstreamTargetJobConfiguration.jobName, Job::class.java), jobLauncher)
        val upstreamJobSchedulerService = LocalDateJobSchedulerService(
            applicationContext.getBean(BookUpstreamRequestJobConfiguration.jobName, Job::class.java), jobLauncher)

        nationalLibraryAPIJobSchedulerService.job = applicationContext
            .getBean(NationalLibraryAPIJobConfiguration.jobName, Job::class.java)
        nationalLibraryAPIJobSchedulerService.jobLauncher = jobLauncher
        nationalLibraryAPIJobSchedulerService.eventPublisher = applicationContext

        naverBookAPIJobSchedulerService.job = applicationContext
            .getBean(NaverBookAPIJobConfiguration.jobName, Job::class.java)
        naverBookAPIJobSchedulerService.jobLauncher = jobLauncher
        naverBookAPIJobSchedulerService.eventPublisher = applicationContext

        kyoboBookRequestJobSchedulerService.eventPublisher = applicationContext
        setUpstreamJobSchedulerService.eventPublisher = applicationContext
        upstreamJobSchedulerService.eventPublisher = applicationContext

        return CompositeJobSchedulerService()
            .addDelegate(nationalLibraryAPIJobSchedulerService)
            .addDelegate(naverBookAPIJobSchedulerService)
            .addDelegate(kyoboBookRequestJobSchedulerService)
            .addDelegate(setUpstreamJobSchedulerService)
            .addDelegate(upstreamJobSchedulerService)
    }
}