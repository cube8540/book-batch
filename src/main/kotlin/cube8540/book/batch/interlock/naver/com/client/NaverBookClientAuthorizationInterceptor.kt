package cube8540.book.batch.interlock.naver.com.client

import feign.RequestInterceptor
import feign.RequestTemplate

class NaverBookClientAuthorizationInterceptor(private val clientId: String, private val clientSecret: String): RequestInterceptor {

    companion object {
        const val clientIdName = "X-Naver-Client-Id"
        const val clientSecretName = "X-Naver-Client-Secret"
    }

    override fun apply(requestTemplate: RequestTemplate) {
        requestTemplate.header(clientIdName, clientId)
        requestTemplate.header(clientSecretName, clientSecret)
    }
}