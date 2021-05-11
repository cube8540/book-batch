package cube8540.book.batch.infra.kyobo.kr

import java.time.LocalDateTime

class KyoboLoginInfo(val cookies: Map<String, String?>, val issuedDateTime: LocalDateTime) {
}