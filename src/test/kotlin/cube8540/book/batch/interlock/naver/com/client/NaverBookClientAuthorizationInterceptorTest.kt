package cube8540.book.batch.interlock.naver.com.client

import feign.RequestTemplate
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class NaverBookClientAuthorizationInterceptorTest {

    private val interceptor = NaverBookClientAuthorizationInterceptor(defaultClientId, defaultClientSecret)

    @Test
    fun `set authorization toke in header`() {
        val template: RequestTemplate = mockk(relaxed = true)

        interceptor.apply(template)
        verify {
            template.header(NaverBookClientAuthorizationInterceptor.clientIdName, defaultClientId)
            template.header(NaverBookClientAuthorizationInterceptor.clientSecretName, defaultClientSecret)
        }
    }
}