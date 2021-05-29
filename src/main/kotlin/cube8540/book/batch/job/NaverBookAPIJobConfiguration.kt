package cube8540.book.batch.job

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.config.APIConnectionProperty
import cube8540.book.batch.config.AuthenticationProperty
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.BookDetailsContext
import cube8540.book.batch.domain.repository.BookDetailsRepository
import cube8540.book.batch.external.naver.com.NaverBookAPIExchanger
import cube8540.book.batch.external.naver.com.NaverBookAPIRequestNames
import cube8540.book.batch.external.naver.com.NaverBookDetailsController
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
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class NaverBookAPIJobConfiguration {

    companion object {
        const val jobName = "naverBookAPICalling"
        const val jobStepName = jobName + "Step"
        const val jobReaderName = jobName + "JobReader"
        const val jobProcessorName = jobName + "JobProcessor"
        const val jobWriterName = jobName + "JobWriter"
        const val defaultChunkSize = 100

        internal var endpointBase = NaverBookAPIRequestNames.endpointBase
    }

    @set:Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @set:Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @set:Autowired
    lateinit var authenticationProperty: AuthenticationProperty

    @set:Autowired
    lateinit var connectionProperty: APIConnectionProperty

    @set:Autowired
    lateinit var jobParameter: BookAPIRequestJobParameter

    @set:[Autowired Qualifier("naverBookAPIObjectMapper")]
    lateinit var objectMapper: ObjectMapper

    @set:Autowired
    lateinit var bookDetailsRepository: BookDetailsRepository

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
        val webClient = WebClient.builder()
            .baseUrl(endpointBase)
            .exchangeStrategies(
                ExchangeStrategies.builder().codecs {
                    it.customCodecs().register(Jackson2JsonEncoder(objectMapper))
                    it.customCodecs().register(Jackson2JsonDecoder(objectMapper))
                }.build()
            )
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(connectionProperty.maxWaitSecond!!.toLong()))
                ))
            .build()

        val exchanger = NaverBookAPIExchanger(webClient, authenticationProperty.naverBook)
        exchanger.retryCount = connectionProperty.retryCount!!
        exchanger.retryDelaySecond = connectionProperty.retryDelaySecond!!

        val reader = WebClientBookReader(exchanger, jobParameter)
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
    fun bookDetailsWriter() = RepositoryBasedBookWriter(bookDetailsRepository, NaverBookDetailsController())
}