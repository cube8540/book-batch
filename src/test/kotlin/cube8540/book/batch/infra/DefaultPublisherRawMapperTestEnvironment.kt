package cube8540.book.batch.infra

import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.Publisher
import cube8540.book.batch.domain.RawProperty
import io.mockk.mockk

object DefaultPublisherRawMapperTestEnvironment {

    internal const val publisherCode0 = "publisher00000"
    internal const val publisherCode1 = "publisher00001"
    internal const val publisherCode2 = "publisher00002"

    internal const val raw00 = "raw00000"
    internal const val raw01 = "raw00001"
    internal const val raw02 = "raw00002"

    internal const val raw10 = "raw10000"
    internal const val raw11 = "raw10001"
    internal const val raw12 = "raw10002"

    internal const val raw20 = "raw20000"
    internal const val raw21 = "raw20001"
    internal const val raw22 = "raw20002"

    internal const val notFoundRaw = "notFoundRaw0000"

    internal val mappingType: MappingType = MappingType.NAVER_BOOK

    private val publisher0 = Publisher(
        code = publisherCode0,
        raws = setOf(RawProperty(raw00, mappingType), RawProperty(raw01, mappingType), RawProperty(raw02, mappingType)),
        keywords = emptySet()
    )
    private val publisher1 = Publisher(
        code = publisherCode1,
        raws = setOf(RawProperty(raw10, mappingType), RawProperty(raw11, mappingType), RawProperty(raw12, mappingType)),
        keywords = emptySet()
    )
    private val publisher2 = Publisher(
        code = publisherCode2,
        raws = setOf(RawProperty(raw20, mappingType), RawProperty(raw21, mappingType), RawProperty(raw22, mappingType)),
        keywords = emptySet()
    )
    internal val publishers = listOf(publisher0, publisher1, publisher2)

    internal val reloadedPublishers: List<Publisher> = listOf(mockk(), mockk(), mockk())
}