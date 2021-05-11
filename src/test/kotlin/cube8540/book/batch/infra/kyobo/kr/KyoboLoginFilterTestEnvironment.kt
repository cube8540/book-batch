package cube8540.book.batch.infra.kyobo.kr

import java.time.LocalDateTime

object KyoboLoginFilterTestEnvironment {

    internal val now = LocalDateTime.of(2021, 5, 5, 20, 35, 0)
    internal val expiredDateTime = now.plusSeconds(KyoboLoginFilter.expiredSeconds.toLong()).plusNanos(1)
    internal val notExpiredDateTime = now.plusSeconds(KyoboLoginFilter.expiredSeconds.toLong()).minusNanos(1)

    internal const val username = "username0001"
    internal const val password = "password0001"

    internal const val setCookieHeader = "Set-Cookie"

    internal const val cookieKey0001 = "cookie0001"
    internal const val cookieKey0002 = "cookie0002"
    internal const val cookieKey0003 = "cookie0003"

    internal const val cookieValue0001 = "cookie0001"
    internal const val cookieValue0002 = "cookie0002"
    internal const val cookieValue0003 = "cookie0003"

    internal const val cookie0001 = "${cookieKey0001}=${cookieValue0001}"
    internal const val cookie0002 = "${cookieKey0002}=${cookieValue0002}"
    internal const val cookie0003 = "${cookieKey0003}=${cookieValue0003}"

}