package cube8540.book.batch.job

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.config.APIConnectionProperty
import cube8540.book.batch.config.AuthenticationProperty
import cube8540.book.batch.config.JobTaskExecutorProperty
import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.BookDetailsContext
import cube8540.book.batch.domain.BookDetailsFilterFunction
import cube8540.book.batch.domain.repository.BookDetailsRepository
import cube8540.book.batch.infra.*
import cube8540.book.batch.external.nl.go.NationalLibraryAPIAuthenticationFilter
import cube8540.book.batch.external.nl.go.NationalLibraryAPIRequestNames
import cube8540.book.batch.external.nl.go.NationalLibraryBookDetailsController
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
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.netty.http.client.HttpClient
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.format.DateTimeFormatter

@Configuration
class NationalLibraryAPIJobConfiguration {

    companion object {
        const val jobName = "nationalLibraryAPICalling"
        const val jobStepName = jobName + "Step"
        const val jobReaderName = jobName + "JobReader"
        const val jobProcessorName = jobName + "JobProcessor"
        const val jobWriterName = jobName + "JobWriter"
        const val defaultChunkSize = 500

        internal var endpoint = URI.create(NationalLibraryAPIRequestNames.endpointBase + NationalLibraryAPIRequestNames.endpointPath)
    }

    @set:Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @set:Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @set:Autowired
    lateinit var authenticationProperty: AuthenticationProperty

    @set:Autowired
    lateinit var jobTaskExecutorProperty: JobTaskExecutorProperty

    @set:Autowired
    lateinit var connectionProperty: APIConnectionProperty

    @set:Autowired
    lateinit var jobParameter: BookAPIRequestJobParameter

    @set:[Autowired Qualifier("nationalLibraryObjectMapper")]
    lateinit var objectMapper: ObjectMapper

    @set:[Autowired Qualifier("nationalLibraryFilterFunction")]
    lateinit var filterFunction: BookDetailsFilterFunction

    @set:Autowired
    lateinit var bookDetailsRepository: BookDetailsRepository

    @set:[Autowired Qualifier("jobTaskExecutor")]
    lateinit var jobTaskExecutor: TaskExecutor

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
        .taskExecutor(jobTaskExecutor)
        .throttleLimit(jobTaskExecutorProperty.throttleLimit!!)
        .build()

    @StepScope
    @Bean(jobReaderName)
    fun bookContextReader(): SynchronizedItemStreamReader<BookDetailsContext> {
        val httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(connectionProperty.maxWaitSecond!!.toLong()))

        val uriBuilder = UriComponentsBuilder.newInstance()
            .uri(endpoint)
            .queryParam(NationalLibraryAPIRequestNames.fromKeyword, jobParameter.from?.format(DateTimeFormatter.BASIC_ISO_DATE))
            .queryParam(NationalLibraryAPIRequestNames.toKeyword, jobParameter.to?.format(DateTimeFormatter.BASIC_ISO_DATE))
            .queryParam(NationalLibraryAPIRequestNames.publisherKeyword, jobParameter.publisher)
            .queryParam(NationalLibraryAPIRequestNames.isbnKeyword, jobParameter.isbn)
            .queryParam(NationalLibraryAPIRequestNames.resultStyle, "json")
            .queryParam(NationalLibraryAPIRequestNames.ebookYN, "N")
            .encode(StandardCharsets.UTF_8)

        val webClient = WebClient.builder()
            .exchangeStrategies(
                ExchangeStrategies.builder().codecs {
                    val decoder = Jackson2JsonDecoder(objectMapper)
                    decoder.maxInMemorySize = -1

                    it.customCodecs().register(decoder)
                    it.customCodecs().register(Jackson2JsonEncoder(objectMapper))
                }.build()
            )
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .filter(NationalLibraryAPIAuthenticationFilter(authenticationProperty.nationalLibrary.key))
            .build()

        val reader = WebClientBookReader(uriBuilder, webClient)
        reader.requestPageParameterName = NationalLibraryAPIRequestNames.pageNumber
        reader.requestPageSizeParameterName = NationalLibraryAPIRequestNames.pageSize
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
                BookDetailsPublisherNonNullProcessor(),
                BookDetailsFilterProcessor(filterFunction)
            )
            .build()

    @StepScope
    @Bean(jobWriterName)
    fun bookDetailsWriter(): SynchronizedItemStreamWriter<BookDetails> {
        val writer = RepositoryBasedBookWriter(bookDetailsRepository, NationalLibraryBookDetailsController())

        val synchronizedItemStreamWriter = SynchronizedItemStreamWriter<BookDetails>()
        synchronizedItemStreamWriter.setDelegate(writer)
        return synchronizedItemStreamWriter
    }

}
