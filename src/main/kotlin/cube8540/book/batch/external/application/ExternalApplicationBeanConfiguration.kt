package cube8540.book.batch.external.application

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.book.application.DefaultBookCommandService
import cube8540.book.batch.book.repository.BookDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ExternalApplicationBeanConfiguration {

    @set:[Autowired Qualifier("defaultObjectMapper")]
    lateinit var objectMapper: ObjectMapper

    @Bean
    fun oauth2ClientRegistrationWebClient(
        clientRegistrations: ReactiveClientRegistrationRepository,
        authorizedClientService: ReactiveOAuth2AuthorizedClientService
    ): WebClient {
        val oauth = ServerOAuth2AuthorizedClientExchangeFilterFunction(
            AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrations, authorizedClientService))
        oauth.setDefaultClientRegistrationId("application")

        return WebClient.builder()
            .exchangeStrategies(
                ExchangeStrategies.builder().codecs {
                    val decoder = Jackson2JsonDecoder(objectMapper)
                    decoder.maxInMemorySize = -1

                    it.customCodecs().register(decoder)
                    it.customCodecs().register(Jackson2JsonEncoder(objectMapper))
                }.build()
            )
            .filter(oauth)
            .build()
    }

    @Bean
    fun externalApplicationBookCommandService(repository: BookDetailsRepository) =
        DefaultBookCommandService(repository, EmptyBookDetailsController())
}