package cube8540.book.batch.job

import cube8540.book.batch.book.application.BookQueryService
import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.external.ExternalBookAPIUpstream
import cube8540.book.batch.job.reader.RepositoryBasedBookReader
import cube8540.book.batch.job.writer.WebClientBookUpstreamWriter
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BookUpstreamRequestJobConfiguration {

    companion object {
        const val jobName = "bookUpstreamRequest"
        const val jobStepName = jobName + "Step"
        const val jobReaderName = jobName + "JobReader"
        const val jobWriterName = jobName + "JobWriter"
        const val defaultChunkSize = 100
    }

    @set:Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @set:Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @set:[Autowired Qualifier("upstreamBookQueryService")]
    lateinit var bookDetailsService: BookQueryService

    @set:[Autowired Qualifier("externalApplicationBookUpstream")]
    lateinit var upstream: ExternalBookAPIUpstream

    @set:Autowired
    lateinit var jobParameter: BookAPIRequestJobParameter

    var chunkSize = defaultChunkSize

    @Bean(jobName)
    fun bookUpstreamRequestJob(): Job = jobBuilderFactory.get(jobName)
        .start(bookUpstreamRequestJobStep())
        .build()

    @JobScope
    @Bean(jobStepName)
    fun bookUpstreamRequestJobStep(): Step = stepBuilderFactory.get(jobStepName)
        .chunk<BookDetails, BookDetails>(chunkSize)
        .reader(bookDetailsReader())
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
    @Bean(jobWriterName)
    fun bookDetailsWriter(): WebClientBookUpstreamWriter = WebClientBookUpstreamWriter(upstream)

}