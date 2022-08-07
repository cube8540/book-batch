package cube8540.book.batch

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import cube8540.book.batch.translator.aladin.kr.client.AladinAuthenticationInfo
import cube8540.book.batch.translator.kyobo.kr.KyoboAuthenticationInfo
import cube8540.book.batch.translator.nl.go.client.NationalLibraryAPIKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
class ObjectMapperConfiguration {

    @Bean
    @Primary
    fun defaultObjectMapper(): ObjectMapper {
        val timeModule = JavaTimeModule()
            .addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateTimeFormatter.ISO_DATE))
            .addSerializer(LocalDate::class.java, LocalDateSerializer(DateTimeFormatter.ISO_DATE))
        return ObjectMapper()
            .registerModule(timeModule)
            .registerModule(KotlinModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
}

@Configuration
class OAuth2Configuration {

    @Bean
    @Primary
    fun appClientRegistrationRepository(properties: OAuth2ClientProperties): ClientRegistrationRepository {
        val providers = properties.provider
        val registers = properties.registration

        val clientRegistrations: List<ClientRegistration> = registers.entries.map { entry ->
            val register = entry.value
            val provider = providers[entry.key]

            val builder = ClientRegistration.withRegistrationId(entry.key)
                .authorizationGrantType(AuthorizationGrantType(register.authorizationGrantType))
                .clientId(register.clientId)
                .clientName(register.clientName)
                .clientSecret(register.clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)

            provider?.let {
                builder.tokenUri(it.tokenUri)
            }

            builder.build()
        }

        return InMemoryClientRegistrationRepository(clientRegistrations)
    }

    @Bean
    @Primary
    fun appOAuth2AuthorizedClientService(@Autowired clientRegistrationRepository: ClientRegistrationRepository): OAuth2AuthorizedClientService =
        InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository)
}

@ConstructorBinding
@ConfigurationProperties(prefix = "api.authentication")
class AuthenticationProperty(
    val nationalLibrary: NationalLibraryAPIKey,
    val kyobo: KyoboAuthenticationInfo,
    val aladin: AladinAuthenticationInfo
)