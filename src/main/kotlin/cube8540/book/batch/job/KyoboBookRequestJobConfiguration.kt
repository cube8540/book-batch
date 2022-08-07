package cube8540.book.batch.job

import cube8540.book.batch.book.application.BookCommandService
import cube8540.book.batch.book.application.BookQueryService
import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.job.reader.RepositoryBasedBookReader
import cube8540.book.batch.job.writer.RepositoryBasedBookWriter
import cube8540.book.batch.translator.kyobo.kr.application.BookDocumentMapper
import cube8540.book.batch.translator.kyobo.kr.application.KyoboClientBookProcessor
import cube8540.book.batch.translator.kyobo.kr.client.KyoboBookClient
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
class KyoboBookRequestJobConfiguration {

    companion object {
        const val jobName = "kyoboBookRequest"
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
    lateinit var kyoboBookClient: KyoboBookClient

    @set:[Autowired Qualifier("kyoboBookDocumentMapper")]
    lateinit var documentMapper: BookDocumentMapper

    @set:[Autowired Qualifier("defaultBookQueryService")]
    lateinit var bookDetailsService: BookQueryService

    @set:[Autowired Qualifier("kyoboBookCommandService")]
    lateinit var bookCommandService: BookCommandService

    @set:Autowired
    lateinit var jobParameter: BookAPIRequestJobParameter

    var chunkSize = defaultChunkSize

    @Bean(jobName)
    fun kyoboBookRequestJob(): Job = jobBuilderFactory.get(jobName)
        .start(kyoboBookRequestJobStep())
        .build()

    @JobScope
    @Bean(jobStepName)
    fun kyoboBookRequestJobStep(): Step = stepBuilderFactory.get(jobStepName)
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
    fun bookDetailsProcessor(): CompositeItemProcessor<BookDetails, BookDetails> {
        return CompositeItemProcessorBuilder<BookDetails, BookDetails>()
            .delegates(KyoboClientBookProcessor(kyoboBookClient, documentMapper))
            .build()
    }

    @StepScope
    @Bean(jobWriterName)
    fun bookDetailsWriter() = RepositoryBasedBookWriter(bookCommandService)
}