package cube8540.book.batch.translator.kyobo.kr.client

import cube8540.book.batch.BatchApplication
import feign.RequestInterceptor
import feign.RequestTemplate
import java.time.Clock
import java.time.LocalDateTime

data class KyoboLoginInfo(val cookies: Map<String, String?>, val issuedDateTime: LocalDateTime)

class KyoboLoginInterceptor(
    private val username: String,
    private val password: String,
    private val client: KyoboLoginClient,
    private val resolver: KyoboCookieResolver = DefaultKyoboCookieResolver()
): RequestInterceptor {

    companion object {
        internal var CLOCK = Clock.system(BatchApplication.DEFAULT_TIME_ZONE.toZoneId())

        const val DEFAULT_REQUEST_COOKIE_HEADER_NAME = "Cookie"
        const val DEFAULT_RESPONSE_COOKIE_HEADER_NAME = "set-cookie"

        const val DEFAULT_EXPIRED_SECONDS = 600 // 10분
    }

    var requestCookieHeader = DEFAULT_REQUEST_COOKIE_HEADER_NAME
    var responseCookieHeader = DEFAULT_RESPONSE_COOKIE_HEADER_NAME

    var expiredSeconds = DEFAULT_EXPIRED_SECONDS

    internal var loginCookie: KyoboLoginInfo? = null

    override fun apply(template: RequestTemplate) {
        if (isLoginExpired()) {
            val response = client.login(KyoboLoginRequest(username, password))

            val cookies = response.headers()[responseCookieHeader]?.let { resolver.resolveCookie(it) } ?: emptyMap()
            this.loginCookie = KyoboLoginInfo(cookies, LocalDateTime.now(CLOCK).plusSeconds(expiredSeconds.toLong()))
        }

        val cookieValue = this.loginCookie?.cookies?.map { "${it.key}=${it.value}" }?.joinToString(";")
        template.header(requestCookieHeader, cookieValue)
    }

    private fun isLoginExpired() = loginCookie?.issuedDateTime?.isBefore(LocalDateTime.now(CLOCK)) != false
}