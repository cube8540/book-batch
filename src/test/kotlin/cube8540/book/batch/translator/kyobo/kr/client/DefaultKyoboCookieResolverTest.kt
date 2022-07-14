package cube8540.book.batch.translator.kyobo.kr.client

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.Test

internal class DefaultKyoboCookieResolverTest {

    private val resolver = DefaultKyoboCookieResolver()

    @Test
    fun `resolve set cookie values`() {
        val setCookies = listOf(
            "cookie1=value1; expires=Sat, 25-Jun-2022 08:22:56 GMT;path=/;httponly",
            "cookie2=value2; path=/",
            "cookie3=value3; expires=Sunday, 25-Jun-2023 08:20:55 GMT; path=/"
        )

        val cookies = resolver.resolveCookie(setCookies)
        assertThat(cookies)
            .containsOnly(entry("cookie1", "value1"), entry("cookie2", "value2"), entry("cookie3", "value3"))
    }
}