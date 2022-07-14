package cube8540.book.batch.translator.kyobo.kr.client

import cube8540.book.batch.BatchApplication.Companion.DEFAULT_TIME_ZONE
import cube8540.book.batch.toDefaultInstance
import feign.RequestTemplate
import feign.Response
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.LocalDateTime

internal class KyoboLoginInterceptorTest {

    private val now = LocalDateTime.of(2022, 6, 27, 1, 0, 0)
    private val clock = Clock.fixed(now.toDefaultInstance(), DEFAULT_TIME_ZONE.toZoneId())

    private val requestCookieHeaderName = "COOKIE_HEADER"
    private val responseCookieHeaderName = "SET_COOKIE"

    private val username = "USER_NAME"
    private val password = "PASSWORD"
    private val expiredSeconds = 100

    private val loginClient: KyoboLoginClient = mockk(relaxed = true)
    private val resolver: KyoboCookieResolver = mockk(relaxed = true)
    private val interceptor = KyoboLoginInterceptor(username, password, loginClient, resolver)

    init {
        KyoboLoginInterceptor.CLOCK = clock

        interceptor.requestCookieHeader = requestCookieHeaderName
        interceptor.responseCookieHeader = responseCookieHeaderName

        interceptor.expiredSeconds = expiredSeconds
    }

    @BeforeEach
    fun setup() {
        configLoginNull()
    }

    @Test
    fun `set authorization header when login info is null`() {
        val loginResponse = makeLoginResponse(
            responseCookieHeaderName,
            "cookie1" to "value1", "cookie2" to "value2", "cookie3" to "value3"
        )

        val requestTemplate: RequestTemplate = mockk(relaxed = true)

        configLoginNull()
        configLoginResponseTo(username, password, loginResponse)

        interceptor.apply(requestTemplate)
        assertThat(interceptor.loginCookie).isEqualTo(KyoboLoginInfo(
            mapOf("cookie1" to "value1", "cookie2" to "value2", "cookie3" to "value3"),
            now.plusSeconds(expiredSeconds.toLong())
        ))
        verify {
            requestTemplate.header(requestCookieHeaderName, "cookie1=value1;cookie2=value2;cookie3=value3")
        }
    }

    @Test
    fun `set authentication header when login info is expired`() {
        val loginInfo = KyoboLoginInfo(emptyMap(), now.minusNanos(1))
        val loginResponse = makeLoginResponse(
            responseCookieHeaderName,
            "cookie1" to "value1", "cookie2" to "value2", "cookie3" to "value3"
        )

        val requestTemplate: RequestTemplate = mockk(relaxed = true)

        configLoginInfo(loginInfo)
        configLoginResponseTo(username, password, loginResponse)

        interceptor.apply(requestTemplate)
        assertThat(interceptor.loginCookie).isEqualTo(KyoboLoginInfo(
            mapOf("cookie1" to "value1", "cookie2" to "value2", "cookie3" to "value3"),
            now.plusSeconds(expiredSeconds.toLong())
        ))
        verify {
            requestTemplate.header(requestCookieHeaderName, "cookie1=value1;cookie2=value2;cookie3=value3")
        }
    }

    @Test
    fun `set authentication header when login info is not expired`() {
        val loginCookie = mapOf("cookie1" to "value1", "cookie2" to "value2", "cookie3" to "value3")
        val loginInfo = KyoboLoginInfo(loginCookie, now.plusNanos(1))

        val requestTemplate: RequestTemplate = mockk(relaxed = true)

        configLoginInfo(loginInfo)

        interceptor.apply(requestTemplate)
        assertThat(interceptor.loginCookie).isEqualTo(loginInfo)
        verify {
            requestTemplate.header(requestCookieHeaderName, "cookie1=value1;cookie2=value2;cookie3=value3")
        }
    }

    @AfterEach
    fun clear() {
        clearMocks(loginClient, resolver)
    }

    private fun configLoginInfo(login: KyoboLoginInfo) {
        interceptor.loginCookie = login
    }

    private fun configLoginNull() {
        interceptor.loginCookie = null
    }

    private fun makeLoginResponse(cookieHeader: String, vararg cookies: Pair<String, String>): Response {
        val responseHeader = HashMap<String, Collection<String>>()

        val cookieValue = cookies.map { "${it.first}=${it.second}" }

        responseHeader[cookieHeader] = cookieValue
        every { resolver.resolveCookie(cookieValue) } returns mapOf(*cookies)

        return mockk(relaxed = true) {
            every { headers() } returns responseHeader
        }
    }

    private fun configLoginResponseTo(username: String, password: String, response: Response) {
        every { loginClient.login(KyoboLoginRequest(username, password)) } returns response
    }
}
