package cube8540.book.batch.translator.kyobo.kr

import java.time.LocalDateTime

data class KyoboLoginInfo(val cookies: Map<String, String?>, val issuedDateTime: LocalDateTime)