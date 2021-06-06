package cube8540.book.batch.external.kyobo.kr

import java.time.LocalDateTime

data class KyoboLoginInfo(val cookies: Map<String, String?>, val issuedDateTime: LocalDateTime)