package cube8540.book.batch.domain

interface PublisherRawMapper {
    fun mapping(raw: String): String?
}

interface DivisionRawMapper {
    fun mapping(raws: List<String>): List<String>
}