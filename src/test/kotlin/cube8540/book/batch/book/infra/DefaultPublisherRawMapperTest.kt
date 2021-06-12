package cube8540.book.batch.book.infra

import cube8540.book.batch.book.domain.createPublisher
import cube8540.book.batch.book.domain.createRaw
import cube8540.book.batch.book.domain.defaultMappingType
import cube8540.book.batch.book.repository.PublisherCustomRepository
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

    private val publisherRepository: PublisherCustomRepository = mockk(relaxed = true)

    @Nested
    inner class InitializationTest {

        @Test
        fun `initialization cache`() {
            val mapper = DefaultPublisherRawMapper(defaultMappingType, publisherRepository)
            val publishers = listOf(createPublisher(code = "publisher01"), createPublisher(code = "publisher02"), createPublisher(code = "publisher03"))

            every { publisherRepository.findByMappingTypeWithRaw(defaultMappingType) } returns publishers
            mapper.afterPropertiesSet()

            assertThat(mapper.cache).isEqualTo(publishers)
        }
    }

    @Nested
    inner class MappingTest {

        @ParameterizedTest
        @MethodSource(value = ["mappedRawAndPublisherCodeProvider"])
        fun `find publisher code by given raw text`(givenRaw: String, resultPublisherCode: String) {
            val mapper = DefaultPublisherRawMapper(defaultMappingType, publisherRepository)
            val publishers = listOf(
                createPublisher(code = "publisher00000", raws = setOf(createRaw("raw00000"), createRaw("raw00001"), createRaw("raw00002"))),
                createPublisher(code = "publisher00001", raws = setOf(createRaw("raw10000"), createRaw("raw10001"), createRaw("raw10002"))),
                createPublisher(code = "publisher00002", raws = setOf(createRaw("raw20000"), createRaw("raw20001"), createRaw("raw20002")))
            )

            every { publisherRepository.findByMappingTypeWithRaw(defaultMappingType) } returns publishers
            mapper.afterPropertiesSet()

            val result = mapper.mapping(givenRaw)
            assertThat(result).isEqualTo(resultPublisherCode)
        }

        @Test
        fun `if not find publisher code returns null`() {
            val mapper = DefaultPublisherRawMapper(defaultMappingType, publisherRepository)
            val publishers = listOf(
                createPublisher(code = "publisher00000", raws = setOf(createRaw("raw00000"), createRaw("raw00001"), createRaw("raw00002"))),
                createPublisher(code = "publisher00001", raws = setOf(createRaw("raw10000"), createRaw("raw10001"), createRaw("raw10002"))),
                createPublisher(code = "publisher00002", raws = setOf(createRaw("raw20000"), createRaw("raw20001"), createRaw("raw20002")))
            )

            every { publisherRepository.findByMappingTypeWithRaw(defaultMappingType) } returns publishers
            mapper.afterPropertiesSet()

            val result = mapper.mapping("NOT FOUND")
            assertThat(result).isNull()
        }

        private fun mappedRawAndPublisherCodeProvider() = Stream.of(
            Arguments.of("raw00000", "publisher00000"),
            Arguments.of("raw00001", "publisher00000"),
            Arguments.of("raw00002", "publisher00000"),
            Arguments.of("raw10000", "publisher00001"),
            Arguments.of("raw10001", "publisher00001"),
            Arguments.of("raw10002", "publisher00001"),
            Arguments.of("raw20000", "publisher00002"),
            Arguments.of("raw20001", "publisher00002"),
            Arguments.of("raw20002", "publisher00002"),
        )
    }
}