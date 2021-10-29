package cube8540.book.batch.job

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.APIConnectionProperty
import cube8540.book.batch.AuthenticationProperty
import cube8540.book.batch.book.application.BookCommandService
import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.book.domain.BookDetailsContext
import cube8540.book.batch.book.domain.BookDetailsFilterFunction
import cube8540.book.batch.external.aladin.kr.AladinAPIExchanger
import cube8540.book.batch.external.aladin.kr.AladinAPIRequestNames
import cube8540.book.batch.job.processor.BookDetailsFilterProcessor
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
class AladinAPIJobConfiguration {

    companion object {
        const val jobName = "aladinAPICalling"
        const val jobStepName = jobName + "Step"
        const val jobReaderName = jobName + "JobReader"
        const val jobProcessorName = jobName + "JobProcessor"
        const val jobWriterName = jobName + "JobWriter"
        const val defaultChunkSize = 50

        internal var endpointBase = AladinAPIRequestNames.endpointBase
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

    @set:[Autowired Qualifier("aladinAPIObjectMapper")]
    lateinit var objectMapper: ObjectMapper

    @set:[Autowired Qualifier("aladinAPIFilterFunction")]
    lateinit var filterFunction: BookDetailsFilterFunction

    @set:[Autowired Qualifier("aladinAPICommandService")]
    lateinit var bookCommandService: BookCommandService

    var chunkSize: Int = defaultChunkSize

    @Bean(jobName)
    fun aladinAPIRequestJob(): Job = jobBuilderFactory.get(jobName)
        .start(aladinAPIRequestJobStep())
        .build()

    @JobScope
    @Bean(jobStepName)
    fun aladinAPIRequestJobStep(): Step = stepBuilderFactory.get(jobStepName)
        .chunk<BookDetailsContext, BookDetails>(chunkSize)
        .reader(bookContextReader())
        .processor(bookDetailsProcessor())
        .writer(bookDetaislWriter())
        .build()

    @StepScope
    @Bean(jobReaderName)
    fun bookContextReader(): WebClientBookReader {
        val webClient = WebClient.builder()
            .baseUrl(endpointBase)
            .exchangeStrategies(
                ExchangeStrategies.builder().codecs {
                    val decoder = Jackson2JsonDecoder(objectMapper)
                    decoder.maxInMemorySize = -1

                    it.customCodecs().register(decoder)
                    it.customCodecs().register(Jackson2JsonEncoder(objectMapper))
                }.build()
            )
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(connectionProperty.maxWaitSecond!!.toLong()))
                ))
            .build()

        val exchanger = AladinAPIExchanger(webClient, authenticationProperty.aladin)
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
                BookDetailsPublisherNonNullProcessor(),
                BookDetailsFilterProcessor(filterFunction)
            )
            .build()

    @StepScope
    @Bean(jobWriterName)
    fun bookDetaislWriter() = RepositoryBasedBookWriter(bookCommandService)
}