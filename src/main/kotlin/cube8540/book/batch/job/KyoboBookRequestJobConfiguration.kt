package cube8540.book.batch.job

import cube8540.book.batch.config.APIConnectionProperty
import cube8540.book.batch.config.AuthenticationProperty
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.repository.BookDetailsRepository
import cube8540.book.batch.external.BookDocumentMapper
import cube8540.book.batch.external.kyobo.kr.KyoboBookDetailsController
import cube8540.book.batch.external.kyobo.kr.KyoboBookRequestNames
import cube8540.book.batch.external.kyobo.kr.KyoboLoginFilter
import cube8540.book.batch.external.kyobo.kr.KyoboWebClientBookProcessor
import cube8540.book.batch.job.reader.RepositoryBasedBookReader
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
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.net.URI
import java.time.Duration

@Configuration
class KyoboBookRequestJobConfiguration {

    companion object {
        const val jobName = "kyoboBookRequest"
        const val jobStepName = jobName + "Step"
        const val jobReaderName = jobName + "JobReader"
        const val jobProcessorName = jobName + "JobProcessor"
        const val jobWriterName = jobName + "JobWriter"
        const val defaultChunkSize = 100

        internal var host = URI.create(KyoboBookRequestNames.kyoboHost)
    }

    @set:Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @set:Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @set:Autowired
    lateinit var authenticationProperty: AuthenticationProperty

    @set:Autowired
    lateinit var connectionProperty: APIConnectionProperty

    @set:[Autowired Qualifier("kyoboBookDocumentMapper")]
    lateinit var documentMapper: BookDocumentMapper

    @set:Autowired
    lateinit var bookDetailsRepository: BookDetailsRepository

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
        val reader = RepositoryBasedBookReader(bookDetailsRepository, jobParameter.from!!, jobParameter.to!!)
        reader.pageSize = chunkSize

        return reader
    }

    @StepScope
    @Bean(jobProcessorName)
    fun bookDetailsProcessor(): CompositeItemProcessor<BookDetails, BookDetails> {
        val kyoboLoginClient = WebClient.builder()
            .baseUrl(host.toString())
            .clientConnector(ReactorClientHttpConnector(defaultHttpClient()))
            .codecs { it.defaultCodecs().maxInMemorySize(-1) }
            .build()

        val kyoboRequestWebClient = WebClient.builder()
            .baseUrl(host.toString())
            .clientConnector(ReactorClientHttpConnector(defaultHttpClient()))
            .codecs { it.defaultCodecs().maxInMemorySize(-1) }
            .filter(KyoboLoginFilter(authenticationProperty.kyobo.username, authenticationProperty.kyobo.password, kyoboLoginClient))
            .build()

        return CompositeItemProcessorBuilder<BookDetails, BookDetails>()
            .delegates(KyoboWebClientBookProcessor(kyoboRequestWebClient, documentMapper))
            .build()
    }

    @StepScope
    @Bean(jobWriterName)
    fun bookDetailsWriter() = RepositoryBasedBookWriter(bookDetailsRepository, KyoboBookDetailsController())

    private fun defaultHttpClient() = HttpClient.create()
        .responseTimeout(Duration.ofSeconds(connectionProperty.maxWaitSecond!!.toLong()))
}