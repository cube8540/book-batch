package cube8540.book.batch.job

import cube8540.book.batch.book.application.BookCommandService
import cube8540.book.batch.book.application.BookQueryService
import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsFilterFunction
import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.repository.BookDetailsRepository
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
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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

    @set:[Autowired Qualifier("unDetachedBookQueryService")]
    lateinit var bookDetailsService: BookQueryService

    @set:[Autowired Qualifier("externalApplicationBookCommandService")]
    lateinit var bookCommandService: BookCommandService

    @set:Autowired
    lateinit var bookDetailsRepository: BookDetailsRepository

    @set:Autowired
    lateinit var jobParameter: BookAPIRequestJobParameter

    @set:[Autowired Qualifier("nationalLibraryFilterFunction")]
    lateinit var nationalLibraryBookDetailsFilterFunction: BookDetailsFilterFunction

    @set:[Autowired Qualifier("naverBookAPIFilterFunction")]
    lateinit var naverBookAPIFilterFunction: BookDetailsFilterFunction

    @set:[Autowired Qualifier("kyoboBookFilterFunction")]
    lateinit var kyoboBookDetailsFilterFunction: BookDetailsFilterFunction

    @set:[Autowired Qualifier("aladinAPIFilterFunction")]
    lateinit var aladinAPIFilterFunction: BookDetailsFilterFunction

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
        .build()

    @StepScope
    @Bean(jobReaderName)
    fun bookDetailsReader(): RepositoryBasedBookReader {
        val reader = RepositoryBasedBookReader(bookDetailsService, jobParameter.from!!, jobParameter.to!!)
        reader.pageSize = chunkSize
        return reader
    }

    @StepScope
    @Bean(jobProcessorName)
    fun bookDetailsProcessor(): CompositeItemProcessor<BookDetails, BookDetails> =
        CompositeItemProcessorBuilder<BookDetails, BookDetails>()
            .delegates(
                BookDetailsOriginalDataApproveProcessor(
                    MappingType.NATIONAL_LIBRARY to nationalLibraryBookDetailsFilterFunction,
                    MappingType.NAVER_BOOK to naverBookAPIFilterFunction,
                    MappingType.KYOBO to kyoboBookDetailsFilterFunction,
                    MappingType.ALADIN to aladinAPIFilterFunction
                ),
                BookSetUpstreamTargetProcessor()
            )
            .build()

    @StepScope
    @Bean(jobWriterName)
    fun bookDetailsWriter(): RepositoryBasedUpstreamTargetWriter = RepositoryBasedUpstreamTargetWriter(bookCommandService)
}