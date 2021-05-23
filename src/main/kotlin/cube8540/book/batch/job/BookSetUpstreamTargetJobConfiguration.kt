package cube8540.book.batch.job

import cube8540.book.batch.config.JobTaskExecutorProperty
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.BookDetailsFilterFunction
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.repository.BookDetailsRepository
import cube8540.book.batch.job.processor.BookDetailsFilterProcessor
import cube8540.book.batch.job.processor.BookDetailsOriginalDataApproveProcessor
import cube8540.book.batch.job.processor.BookSetUpstreamTargetProcessor
import cube8540.book.batch.job.reader.RepositoryBasedBookReader
import cube8540.book.batch.job.writer.RepositoryBasedUpstreamTargetWriter
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.support.CompositeItemProcessor
import org.springframework.batch.item.support.SynchronizedItemStreamReader
import org.springframework.batch.item.support.SynchronizedItemStreamWriter
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor

@Configuration
class BookSetUpstreamTargetJobConfiguration {

    companion object {
        const val jobName = "bookSetUpstreamTarget"
        const val jobStepName = jobName + "Step"
        const val jobReaderName = jobName + "JobReader"
        const val jobProcessorName = jobName + "JobProcessor"
        const val jobWriterName = jobName + "JobWriter"
        const val defaultChunkSize = 1000
    }

    @set:Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @set:Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @set:Autowired
    lateinit var jobTaskExecutorProperty: JobTaskExecutorProperty

    @set:Autowired
    lateinit var bookDetailsRepository: BookDetailsRepository

    @set:Autowired
    lateinit var jobParameter: BookAPIRequestJobParameter

    @set:[Autowired Qualifier("jobTaskExecutor")]
    lateinit var jobTaskExecutor: TaskExecutor

    @set:[Autowired Qualifier("nationalLibraryFilterFunction")]
    lateinit var nationalLibraryBookDetailsFilterFunction: BookDetailsFilterFunction

    @set:[Autowired Qualifier("naverBookAPIFilterFunction")]
    lateinit var naverBookAPIFilterFunction: BookDetailsFilterFunction

    @set:[Autowired Qualifier("kyoboBookFilterFunction")]
    lateinit var kyoboBookDetailsFilterFunction: BookDetailsFilterFunction

    var chunkSize = defaultChunkSize

    @Bean(jobName)
    fun bookSetUpstreamJob(): Job = jobBuilderFactory.get(jobName)
        .start(bookSetUpstreamJobStep())
        .build()

    @JobScope
    @Bean(jobStepName)
    fun bookSetUpstreamJobStep(): Step = stepBuilderFactory.get(jobStepName)
        .chunk<BookDetails, BookDetails>(chunkSize)
        .reader(bookDetailsReader())
        .processor(bookDetailsProcessor())
        .writer(bookDetailsWriter())
        .taskExecutor(jobTaskExecutor)
        .throttleLimit(jobTaskExecutorProperty.throttleLimit!!)
        .build()

    @StepScope
    @Bean(jobReaderName)
    fun bookDetailsReader(): SynchronizedItemStreamReader<BookDetails> {
        val reader = RepositoryBasedBookReader(bookDetailsRepository, jobParameter.from!!, jobParameter.to!!)
        reader.pageSize = chunkSize

        val synchronizedItemStreamReader = SynchronizedItemStreamReader<BookDetails>()
        synchronizedItemStreamReader.setDelegate(reader)
        return synchronizedItemStreamReader
    }

    @StepScope
    @Bean(jobProcessorName)
    fun bookDetailsProcessor(): CompositeItemProcessor<BookDetails, BookDetails> =
        CompositeItemProcessorBuilder<BookDetails, BookDetails>()
            .delegates(
                BookDetailsOriginalDataApproveProcessor(
                    MappingType.NATIONAL_LIBRARY to nationalLibraryBookDetailsFilterFunction,
                    MappingType.NAVER_BOOK to naverBookAPIFilterFunction,
                    MappingType.KYOBO to kyoboBookDetailsFilterFunction
                ),
                BookSetUpstreamTargetProcessor()
            )
            .build()

    @StepScope
    @Bean(jobWriterName)
    fun bookDetailsWriter(): SynchronizedItemStreamWriter<BookDetails> {
        val writer = RepositoryBasedUpstreamTargetWriter(bookDetailsRepository)

        val synchronizedItemStreamWriter = SynchronizedItemStreamWriter<BookDetails>()
        synchronizedItemStreamWriter.setDelegate(writer)
        return synchronizedItemStreamWriter
    }
}