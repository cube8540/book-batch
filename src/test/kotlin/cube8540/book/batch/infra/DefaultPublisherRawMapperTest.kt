package cube8540.book.batch.infra

import cube8540.book.batch.domain.repository.PublisherCustomRepository
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.mappingType
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.notFoundRaw
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.publisherCode0
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.publisherCode1
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.publisherCode2
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.publishers
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.raw00
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.raw01
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.raw02
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.raw10
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.raw11
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.raw12
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.raw20
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.raw21
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.raw22
import cube8540.book.batch.infra.DefaultPublisherRawMapperTestEnvironment.reloadedPublishers
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class DefaultPublisherRawMapperTest {

    private val publisherRepository: PublisherCustomRepository = mockk()

    private val publisherRawMapper: DefaultPublisherRawMapper

    init {
        every { publisherRepository.findByMappingTypeWithRaw(mappingType) } returns publishers
        publisherRawMapper = DefaultPublisherRawMapper(mappingType, publisherRepository)
    }

    @Nested
    inner class InitializationTest {

        @Test
        fun `initialization cache`() {
            assertThat(publisherRawMapper.cache).isEqualTo(publishers)
        }
    }

    @Nested
    inner class MappingTest {

        @ParameterizedTest
        @MethodSource(value = ["mappedRawAndPublisherCodeProvider"])
        fun `find publisher code by given raw text`(givenRaw: String, resultPublisherCode: String) {
            val result = publisherRawMapper.mapping(givenRaw)

            assertThat(result).isEqualTo(resultPublisherCode)
        }

        @Test
        fun `if not find publisher code returns null`() {
            val result = publisherRawMapper.mapping(notFoundRaw)

            assertThat(result).isNull()
        }

        private fun mappedRawAndPublisherCodeProvider() = Stream.of(
            Arguments.of(raw00, publisherCode0),
            Arguments.of(raw01, publisherCode0),
            Arguments.of(raw02, publisherCode0),
            Arguments.of(raw10, publisherCode1),
            Arguments.of(raw11, publisherCode1),
            Arguments.of(raw12, publisherCode1),
            Arguments.of(raw20, publisherCode2),
            Arguments.of(raw21, publisherCode2),
            Arguments.of(raw22, publisherCode2),
        )
    }

    @Nested
    inner class ReloadTest {

        @Test
        fun `reload publisher`() {
            every { publisherRepository.findByMappingTypeWithRaw(mappingType) } returns reloadedPublishers

            publisherRawMapper.reload()
            assertThat(publisherRawMapper.cache).isEqualTo(reloadedPublishers)
        }
    }
}