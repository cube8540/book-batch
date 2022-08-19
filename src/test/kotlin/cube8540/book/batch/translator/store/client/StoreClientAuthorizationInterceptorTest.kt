package cube8540.book.batch.translator.store.client

import cube8540.book.batch.translator.store.OAuth2Provider
import feign.RequestTemplate
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class StoreClientAuthorizationInterceptorTest {

    private val applicationId = "APPLICATION_ID"
    private val provider: OAuth2Provider = mockk()

    private val interceptor = StoreClientAuthorizationInterceptor(applicationId, provider)

    @Test
    fun `set authorization token to header`() {
        val authorizationToken = "AUTHORIZATION_TOKEN"

        every { provider.getAuthenticationToken(applicationId) } returns authorizationToken

        val requestTemplate: RequestTemplate = mockk(relaxed = true)
        interceptor.apply(requestTemplate)
        verify {
            requestTemplate.header("Authorization", "bearer $authorizationToken")
        }
    }
}