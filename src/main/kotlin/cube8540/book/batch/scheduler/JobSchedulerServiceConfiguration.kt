package cube8540.book.batch.scheduler

import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.repository.PublisherRepository
import cube8540.book.batch.job.BookSetUpstreamTargetJobConfiguration
import cube8540.book.batch.job.KyoboBookRequestJobConfiguration
import cube8540.book.batch.job.NationalLibraryAPIJobConfiguration
import cube8540.book.batch.job.NaverBookAPIJobConfiguration
import cube8540.book.batch.scheduler.impl.LocalDateJobSchedulerService
import cube8540.book.batch.scheduler.impl.LocalDateWithPublisherSchedulerService
import org.springframework.batch.core.Job
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JobSchedulerServiceConfiguration {

    @Bean
    fun nationalLibraryAPIJobSchedulerService(
        @Qualifier(NationalLibraryAPIJobConfiguration.jobName) job: Job, jobLauncher: JobLauncher, publisherRepository: PublisherRepository
    ): JobSchedulerService {
        val jobSchedulerService = LocalDateWithPublisherSchedulerService(MappingType.NATIONAL_LIBRARY, publisherRepository)

        jobSchedulerService.job = job
        jobSchedulerService.jobLauncher = jobLauncher
        return jobSchedulerService
    }

    @Bean
    fun naverBookAPIJobSchedulerService(
        @Qualifier(NaverBookAPIJobConfiguration.jobName) job: Job, jobLauncher: JobLauncher, publisherRepository: PublisherRepository
    ): JobSchedulerService {
        val jobSchedulerService = LocalDateWithPublisherSchedulerService(MappingType.NAVER_BOOK, publisherRepository)

        jobSchedulerService.job = job
        jobSchedulerService.jobLauncher = jobLauncher
        return jobSchedulerService
    }

    @Bean
    fun kyoboBookRequestJobSchedulerService(
        @Qualifier(KyoboBookRequestJobConfiguration.jobName) job: Job, jobLauncher: JobLauncher
    ): JobSchedulerService = LocalDateJobSchedulerService(job, jobLauncher)

    @Bean
    fun setUpstreamTargetJobSchedulerService(
        @Qualifier(BookSetUpstreamTargetJobConfiguration.jobName) job: Job, jobLauncher: JobLauncher
    ): JobSchedulerService = LocalDateJobSchedulerService(job, jobLauncher)

}