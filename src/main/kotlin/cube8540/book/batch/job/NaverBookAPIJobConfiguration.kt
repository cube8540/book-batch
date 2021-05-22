package cube8540.book.batch.job

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.config.APIConnectionProperty
import cube8540.book.batch.config.AuthenticationProperty
import cube8540.book.batch.config.JobTaskExecutorProperty
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.BookDetailsContext
import cube8540.book.batch.domain.repository.BookDetailsRepository
import cube8540.book.batch.infra.*
import cube8540.book.batch.infra.naver.com.NaverBookAPIAuthenticationFilter
import cube8540.book.batch.infra.naver.com.NaverBookAPIPageDecision
import cube8540.book.batch.infra.naver.com.NaverBookAPIRequestNames
import cube8540.book.batch.infra.naver.com.NaverBookDetailsController
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
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.netty.http.client.HttpClient
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.format.DateTimeFormatter

@Configuration
class NaverBookAPIJobConfiguration {

    companion object {
        const val jobName = "naverBookAPICalling"
        const val jobStepName = jobName + "Step"
        const val jobReaderName = jobName + "JobReader"
        const val jobProcessorName = jobName + "JobProcessor"
        const val jobWriterName = jobName + "JobWriter"
        const val defaultChunkSize = 100

        internal var endpoint = URI.create(NaverBookAPIRequestNames.endpointBase + NaverBookAPIRequestNames.endpointPath)
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
    lateinit var jobExecutorProperty: JobTaskExecutorProperty

    @set:Autowired
    lateinit var jobParameter: BookAPIRequestJobParameter

    @set:[Autowired Qualifier("naverBookAPIObjectMapper")]
    lateinit var objectMapper: ObjectMapper

    @set:Autowired
    lateinit var bookDetailsRepository: BookDetailsRepository

    @set:[Autowired Qualifier("jobTaskExecutor")]
    lateinit var jobTaskExecutor: TaskExecutor

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
        .taskExecutor(jobTaskExecutor)
        .throttleLimit(jobExecutorProperty.throttleLimit!!)
        .build()

    @StepScope
    @Bean(jobReaderName)
    fun bookContextReader(): SynchronizedItemStreamReader<BookDetailsContext> {
        val httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(connectionProperty.maxWaitSecond!!.toLong()))

        val uriBuilder = UriComponentsBuilder.newInstance()
            .uri(endpoint)
            .queryParam(NaverBookAPIRequestNames.fromKeyword, jobParameter.from?.format(DateTimeFormatter.BASIC_ISO_DATE))
            .queryParam(NaverBookAPIRequestNames.toKeyword, jobParameter.to?.format(DateTimeFormatter.BASIC_ISO_DATE))
            .queryParam(NaverBookAPIRequestNames.publisherKeyword, jobParameter.publisher)
            .queryParam(NaverBookAPIRequestNames.isbnKeyword, jobParameter.isbn)
            .encode(StandardCharsets.UTF_8)

        val webClient = WebClient.builder()
            .exchangeStrategies(
                ExchangeStrategies.builder().codecs {
                    it.customCodecs().register(Jackson2JsonEncoder(objectMapper))
                    it.customCodecs().register(Jackson2JsonDecoder(objectMapper))
                }.build()
            )
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .filter(NaverBookAPIAuthenticationFilter(authenticationProperty.naverBook.clientId, authenticationProperty.naverBook.clientSecret))
            .build()

        val reader = WebClientBookReader(uriBuilder, webClient)
        reader.pageDecision = NaverBookAPIPageDecision()
        reader.requestPageParameterName = NaverBookAPIRequestNames.start
        reader.requestPageSizeParameterName = NaverBookAPIRequestNames.display
        reader.isSaveState = false
        reader.pageSize = chunkSize
        reader.retryDelaySecond = connectionProperty.retryDelaySecond!!
        reader.retryCount = connectionProperty.retryCount!!

        val synchronizedItemStreamReader = SynchronizedItemStreamReader<BookDetailsContext>()
        synchronizedItemStreamReader.setDelegate(reader)
        return synchronizedItemStreamReader
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
    fun bookDetailsWriter(): SynchronizedItemStreamWriter<BookDetails> {
        val writer = RepositoryBasedBookWriter(bookDetailsRepository, NaverBookDetailsController())

        val synchronizedItemStreamWriter = SynchronizedItemStreamWriter<BookDetails>()
        synchronizedItemStreamWriter.setDelegate(writer)
        return synchronizedItemStreamWriter
    }
}