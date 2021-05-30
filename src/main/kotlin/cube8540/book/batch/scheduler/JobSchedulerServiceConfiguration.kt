package cube8540.book.batch.scheduler

import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.repository.PublisherRepository
import cube8540.book.batch.job.BookSetUpstreamTargetJobConfiguration
import cube8540.book.batch.job.KyoboBookRequestJobConfiguration
import cube8540.book.batch.job.NationalLibraryAPIJobConfiguration
import cube8540.book.batch.job.NaverBookAPIJobConfiguration
import cube8540.book.batch.scheduler.application.CompositeJobSchedulerService
import cube8540.book.batch.scheduler.application.JobSchedulerService
import cube8540.book.batch.scheduler.application.LocalDateJobSchedulerService
import cube8540.book.batch.scheduler.application.LocalDateWithPublisherSchedulerService
import org.springframework.batch.core.Job
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JobSchedulerServiceConfiguration {

    @set:[Autowired Qualifier(NationalLibraryAPIJobConfiguration.jobName)]
    lateinit var nationalLibraryAPIJob: Job

    @set:[Autowired Qualifier(NaverBookAPIJobConfiguration.jobName)]
    lateinit var naverBookAPIJob: Job

    @set:[Autowired Qualifier(KyoboBookRequestJobConfiguration.jobName)]
    lateinit var kyoboBookRequestJob: Job

    @set:[Autowired Qualifier(BookSetUpstreamTargetJobConfiguration.jobName)]
    lateinit var setUpstreamTargetJob: Job

    @set:Autowired
    lateinit var jobLauncher: JobLauncher

    @set:Autowired
    lateinit var publisherRepository: PublisherRepository

    @set:Autowired
    lateinit var eventPublisher: ApplicationEventPublisher

    @Bean
    fun jobSchedulerService(): JobSchedulerService {
        val nationalLibraryAPIJobSchedulerService = LocalDateWithPublisherSchedulerService(MappingType.NATIONAL_LIBRARY, publisherRepository)
        val naverBookAPIJobSchedulerService = LocalDateWithPublisherSchedulerService(MappingType.NAVER_BOOK, publisherRepository)
        val kyoboBookRequestJobSchedulerService = LocalDateJobSchedulerService(kyoboBookRequestJob, jobLauncher)
        val setUpstreamJobSchedulerService = LocalDateJobSchedulerService(setUpstreamTargetJob, jobLauncher)

        nationalLibraryAPIJobSchedulerService.job = nationalLibraryAPIJob
        nationalLibraryAPIJobSchedulerService.jobLauncher = jobLauncher
        nationalLibraryAPIJobSchedulerService.eventPublisher = eventPublisher

        naverBookAPIJobSchedulerService.job = naverBookAPIJob
        naverBookAPIJobSchedulerService.jobLauncher = jobLauncher
        naverBookAPIJobSchedulerService.eventPublisher = eventPublisher

        kyoboBookRequestJobSchedulerService.eventPublisher = eventPublisher
        setUpstreamJobSchedulerService.eventPublisher = eventPublisher

        return CompositeJobSchedulerService()
            .addDelegate(nationalLibraryAPIJobSchedulerService)
            .addDelegate(naverBookAPIJobSchedulerService)
            .addDelegate(kyoboBookRequestJobSchedulerService)
            .addDelegate(setUpstreamJobSchedulerService)
    }
}