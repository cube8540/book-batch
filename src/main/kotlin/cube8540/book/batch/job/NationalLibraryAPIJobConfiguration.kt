package cube8540.book.batch.job

import cube8540.book.batch.AuthenticationProperty
import cube8540.book.batch.book.application.BookCommandService
import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsContext
import cube8540.book.batch.book.domain.BookDetailsFilterFunction
import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.job.processor.BookDetailsFilterProcessor
import cube8540.book.batch.job.processor.BookDetailsIsbnNonNullProcessor
import cube8540.book.batch.job.processor.BookDetailsPublisherNonNullProcessor
import cube8540.book.batch.job.processor.ContextToBookDetailsProcessor
import cube8540.book.batch.job.reader.WebClientBookReader
import cube8540.book.batch.job.writer.RepositoryBasedBookWriter
import cube8540.book.batch.translator.nl.go.application.NationalLibraryExchanger
import cube8540.book.batch.translator.nl.go.client.NationalLibraryClient
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
class NationalLibraryAPIJobConfiguration {

    companion object {
        const val jobName = "nationalLibraryAPICalling"
        const val jobStepName = jobName + "Step"
        const val jobReaderName = jobName + "JobReader"
        const val jobProcessorName = jobName + "JobProcessor"
        const val jobWriterName = jobName + "JobWriter"
        const val defaultChunkSize = 500
    }

    @set:Autowired
    lateinit var client: NationalLibraryClient

    @set:[Autowired Qualifier("nationalLibraryPublisherRawMapper")]
    lateinit var publisherRawMapper: PublisherRawMapper

    @set:Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @set:Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @set:Autowired
    lateinit var authenticationProperty: AuthenticationProperty

    @set:Autowired
    lateinit var jobParameter: BookAPIRequestJobParameter

    @set:[Autowired Qualifier("nationalLibraryFilterFunction")]
    lateinit var filterFunction: BookDetailsFilterFunction

    @set:[Autowired Qualifier("nationalLibraryBookCommandService")]
    lateinit var bookCommandService: BookCommandService

    var chunkSize: Int = defaultChunkSize

    @Bean(jobName)
    fun nationalLibraryAPIRequestJob(): Job = jobBuilderFactory.get(jobName)
        .start(nationalLibraryAPIRequestJobStep())
        .build()

    @JobScope
    @Bean(jobStepName)
    fun nationalLibraryAPIRequestJobStep(): Step = stepBuilderFactory.get(jobStepName)
        .chunk<BookDetailsContext, BookDetails>(chunkSize)
        .reader(bookContextReader())
        .processor(bookDetailsProcessor())
        .writer(bookDetailsWriter())
        .build()

    @StepScope
    @Bean(jobReaderName)
    fun bookContextReader(): WebClientBookReader {
        val reader = WebClientBookReader(NationalLibraryExchanger(client, publisherRawMapper, authenticationProperty.nationalLibrary), jobParameter)
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
                BookDetailsPublisherNonNullProcessor(),
                BookDetailsFilterProcessor(filterFunction)
            )
            .build()

    @StepScope
    @Bean(jobWriterName)
    fun bookDetailsWriter() = RepositoryBasedBookWriter(bookCommandService)

}
