package cube8540.book.batch.config

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.batch.external.BookDocumentMapper
import cube8540.book.batch.external.kyobo.kr.KyoboBookRequestNames
import cube8540.book.batch.external.kyobo.kr.KyoboLoginFilter
import cube8540.book.batch.external.naver.com.NaverBookAPIAuthenticationFilter
import cube8540.book.batch.external.naver.com.NaverBookAPIPageDecisionFilter
import cube8540.book.batch.external.naver.com.NaverBookAPIRequestNames
import cube8540.book.batch.external.nl.go.NationalLibraryAPIAuthenticationFilter
import cube8540.book.batch.external.nl.go.NationalLibraryAPIRequestNames
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration(private val authenticationProperty: AuthenticationProperty) {

    @Bean
    fun naverWebClient(@Qualifier("naverBookAPIObjectMapper") objectMapper: ObjectMapper) = WebClient.builder()
        .baseUrl(NaverBookAPIRequestNames.endpointBase)
        .exchangeStrategies(
            ExchangeStrategies.builder().codecs {
                it.customCodecs().register(Jackson2JsonEncoder(objectMapper))
                it.customCodecs().register(Jackson2JsonDecoder(objectMapper))
            }.build()
        )
        .filter(NaverBookAPIAuthenticationFilter(
            authenticationProperty.naverBook.clientId,
            authenticationProperty.naverBook.clientSecret
        ))
        .filter(NaverBookAPIPageDecisionFilter())
        .build()

    @Bean
    fun nationalLibraryWebClient(@Qualifier("nationalLibraryObjectMapper") objectMapper: ObjectMapper) = WebClient.builder()
        .baseUrl(NationalLibraryAPIRequestNames.endpointBase)
        .exchangeStrategies(
            ExchangeStrategies.builder().codecs {
                it.customCodecs().register(Jackson2JsonEncoder(objectMapper))
                it.customCodecs().register(Jackson2JsonDecoder(objectMapper))
            }.build()
        )
        .filter(NationalLibraryAPIAuthenticationFilter(authenticationProperty.nationalLibrary.key))
        .build()

    @Bean
    fun kyoboWebClient(
        @Qualifier("kyoboBookDocumentMapper") documentMapper: BookDocumentMapper
    ): WebClient {
        val kyoboLoginWebClient = WebClient.builder()
            .baseUrl(KyoboBookRequestNames.kyoboHost)
            .build()

        return WebClient.builder()
            .baseUrl(KyoboBookRequestNames.kyoboHost)
            .codecs { it.defaultCodecs().maxInMemorySize(-1) }
            .filter(KyoboLoginFilter(
                authenticationProperty.kyobo.username,
                authenticationProperty.kyobo.password,
                kyoboLoginWebClient
            ))
            .build()
    }
}