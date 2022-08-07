package cube8540.book.batch.translator.store

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.core.OAuth2AccessToken
import java.util.*

internal class DefaultOAuth2ProviderTest {

    private val manager: OAuth2AuthorizedClientManager = mockk(relaxed = true)

    private val provider = DefaultOAuth2Provider(manager)

    @Test
    fun `get authorized token by given service name`() {
        val serviceName = "SERVICE_NAME"
        val tokenValue = UUID.randomUUID().toString()

        val requestCaptor = slot<OAuth2AuthorizeRequest>()
        val oauth2AuthorizedClient = createOAuth2AuthorizedClient(tokenValue)

        every { manager.authorize(capture(requestCaptor)) } returns oauth2AuthorizedClient

        val tokenResult = provider.getAuthenticationToken(serviceName)
        assertThat(requestCaptor.captured.clientRegistrationId).isEqualTo(serviceName)
        assertThat(requestCaptor.captured.principal).isEqualTo(DefaultOAuth2Provider.ANONYMOUS_USER_AUTHENTICATION)
        assertThat(tokenResult).isEqualTo(tokenValue)
    }

    private fun createOAuth2AuthorizedClient(tokenValue: String): OAuth2AuthorizedClient {
        return mockk(relaxed = true) {
            every { accessToken } returns createAccessToken(tokenValue)
        }
    }

    private fun createAccessToken(value: String): OAuth2AccessToken = mockk(relaxed = true) {
        every { tokenValue } returns value
    }
}