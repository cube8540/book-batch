package cube8540.book.batch.translator.store

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository

@Configuration
class StoreBeanConfiguration {

    @Bean
    @Autowired
    fun oauth2Provider(
        @Qualifier("appClientRegistrationRepository") clientRegistrationRepository: ClientRegistrationRepository,
        @Qualifier("appOAuth2AuthorizedClientService") authorizedClientService: OAuth2AuthorizedClientService
    ): OAuth2Provider = DefaultOAuth2Provider(AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService))

}