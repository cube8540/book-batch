package cube8540.book.batch.infra

import cube8540.book.batch.domain.BookDetailsFilterIdGenerator
import java.util.*

class DefaultBookDetailsFilterIdGenerator: BookDetailsFilterIdGenerator {
    override fun generate(): String = UUID.randomUUID().toString().replace("-", "")
}