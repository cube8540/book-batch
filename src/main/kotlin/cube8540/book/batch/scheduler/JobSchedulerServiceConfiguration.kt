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
    fun regularJobSchedulerService(): JobSchedulerService = CompositeJobSchedulerService()
        .addDelegate(createNationalLibraryAPIJobSchedulerService())
        .addDelegate(createAladinBookAPIJobSchedulerService())
        .addDelegate(createKyoboBookRequestJobSchedulerService())
        .addDelegate(createSetUpstreamJobSchedulerService())
        .addDelegate(createUpstreamJobSchedulerService())

    @Bean
    fun nonRegularJobSchedulerService(): JobSchedulerService = CompositeJobSchedulerService()
        .addDelegate(createNationalLibraryAPIJobSchedulerService())
        .addDelegate(createKyoboBookRequestJobSchedulerService())
        .addDelegate(createSetUpstreamJobSchedulerService())
        .addDelegate(createUpstreamJobSchedulerService())

    private fun createNationalLibraryAPIJobSchedulerService(): LocalDateWithPublisherSchedulerService {
        val nationalLibraryAPIJobSchedulerService = LocalDateWithPublisherSchedulerService(MappingType.NATIONAL_LIBRARY, publisherRepository)

        nationalLibraryAPIJobSchedulerService.job = applicationContext
            .getBean(NationalLibraryAPIJobConfiguration.jobName, Job::class.java)
        nationalLibraryAPIJobSchedulerService.jobLauncher = jobLauncher
        nationalLibraryAPIJobSchedulerService.eventPublisher = applicationContext

        return nationalLibraryAPIJobSchedulerService
    }

    private fun createAladinBookAPIJobSchedulerService(): LocalDateWithPublisherSchedulerService {
        val aladinAPIJobSchedulerService = LocalDateWithPublisherSchedulerService(MappingType.ALADIN, publisherRepository)

        aladinAPIJobSchedulerService.job = applicationContext
            .getBean(AladinAPIJobConfiguration.jobName, Job::class.java)
        aladinAPIJobSchedulerService.jobLauncher = jobLauncher
        aladinAPIJobSchedulerService.eventPublisher = applicationContext

        return aladinAPIJobSchedulerService
    }

    private fun createKyoboBookRequestJobSchedulerService(): LocalDateJobSchedulerService {
        val kyoboBookRequestJobSchedulerService = LocalDateJobSchedulerService(
            applicationContext.getBean(KyoboBookRequestJobConfiguration.jobName, Job::class.java), jobLauncher)

        kyoboBookRequestJobSchedulerService.eventPublisher = applicationContext

        return kyoboBookRequestJobSchedulerService
    }

    private fun createSetUpstreamJobSchedulerService(): LocalDateJobSchedulerService {
        val setUpstreamJobSchedulerService = LocalDateJobSchedulerService(
            applicationContext.getBean(BookSetUpstreamTargetJobConfiguration.jobName, Job::class.java), jobLauncher)

        setUpstreamJobSchedulerService.eventPublisher = applicationContext

        return setUpstreamJobSchedulerService
    }

    private fun createUpstreamJobSchedulerService(): LocalDateJobSchedulerService {
        val upstreamJobSchedulerService = LocalDateJobSchedulerService(
            applicationContext.getBean(BookUpstreamRequestJobConfiguration.jobName, Job::class.java), jobLauncher)

        upstreamJobSchedulerService.eventPublisher = applicationContext

        return upstreamJobSchedulerService
    }
}