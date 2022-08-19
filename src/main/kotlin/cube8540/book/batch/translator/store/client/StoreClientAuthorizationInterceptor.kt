package cube8540.book.batch.translator.store.client

import cube8540.book.batch.translator.store.OAuth2Provider
import feign.RequestInterceptor
import feign.RequestTemplate

class StoreClientAuthorizationInterceptor(
    private val applicationId: String,
    private val provider: OAuth2Provider
): RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        provider.getAuthenticationToken(applicationId)?.let {
            template.header("Authorization", "bearer $it")
        }
    }
}