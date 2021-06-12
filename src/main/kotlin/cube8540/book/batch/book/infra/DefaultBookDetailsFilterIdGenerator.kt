package cube8540.book.batch.book.infra

import cube8540.book.batch.book.domain.BookDetailsFilterIdGenerator
import java.util.*

class DefaultBookDetailsFilterIdGenerator: BookDetailsFilterIdGenerator {
    override fun generate(): String = UUID.randomUUID().toString().replace("-", "")
}