package cube8540.book.batch.job

import cube8540.book.batch.book.application.BookCommandService
import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsContext
import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.interlock.naver.com.application.NaverBookExchanger
import cube8540.book.batch.interlock.naver.com.application.NaverBookPageDecision
import cube8540.book.batch.interlock.naver.com.client.NaverBookClient
import cube8540.book.batch.job.processor.BookDetailsIsbnNonNullProcessor
import cube8540.book.batch.job.processor.BookDetailsPublisherNonNullProcessor
import cube8540.book.batch.job.processor.ContextToBookDetailsProcessor
import cube8540.book.batch.job.reader.WebClientBookReader
import cube8540.book.batch.job.writer.RepositoryBasedBookWriter
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
class NaverBookAPIJobConfiguration {

    companion object {
        const val jobName = "naverBookAPICalling"
        const val jobStepName = jobName + "Step"
        const val jobReaderName = jobName + "JobReader"
        const val jobProcessorName = jobName + "JobProcessor"
        const val jobWriterName = jobName + "JobWriter"
        const val defaultChunkSize = 100
    }

    @set:Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @set:Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @set:Autowired
    lateinit var client: NaverBookClient

    @set:[Autowired Qualifier("naverPublisherRawMapper")]
    lateinit var publisherRawMapper: PublisherRawMapper

    @set:Autowired
    lateinit var jobParameter: BookAPIRequestJobParameter

    @set:[Autowired Qualifier("naverBookAPICommandService")]
    lateinit var bookCommandService: BookCommandService

    var chunkSize = defaultChunkSize

    @Bean(jobName)
    fun naverBookAPIRequestJob(): Job = jobBuilderFactory.get(jobName)
        .start(naverBookAPIRequestJobStop())
        .build()

    @JobScope
    @Bean(jobStepName)
    fun naverBookAPIRequestJobStop(): Step = stepBuilderFactory.get(jobStepName)
        .chunk<BookDetailsContext, BookDetails>(chunkSize)
        .reader(bookContextReader())
        .processor(bookDetailsProcessor())
        .writer(bookDetailsWriter())
        .build()

    @StepScope
    @Bean(jobReaderName)
    fun bookContextReader(): WebClientBookReader {
        val reader = WebClientBookReader(NaverBookExchanger(client, NaverBookPageDecision(), publisherRawMapper), jobParameter)
        reader.isSaveState = false
        reader.pageSize = chunkSize

        return reader
    }

    @StepScope
    @Bean(jobProcessorName)
    fun bookDetailsProcessor(): CompositeItemProcessor<BookDetailsContext, BookDetails> =
        CompositeItemProcessorBuilder<BookDetailsContext, BookDetails>()
            .delegates(
                ContextToBookDetailsProcessor(),
                BookDetailsIsbnNonNullProcessor(),
                BookDetailsPublisherNonNullProcessor()
            )
            .build()

    @StepScope
    @Bean(jobWriterName)
    fun bookDetailsWriter() = RepositoryBasedBookWriter(bookCommandService)
}