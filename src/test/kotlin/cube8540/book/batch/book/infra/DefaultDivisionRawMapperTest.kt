package cube8540.book.batch.book.infra

import cube8540.book.batch.book.domain.createDivision
import cube8540.book.batch.book.domain.createRaw
import cube8540.book.batch.book.domain.defaultMappingType
import cube8540.book.batch.book.repository.DivisionCustomRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class DefaultDivisionRawMapperTest {

    private val divisionRepository: DivisionCustomRepository = mockk(relaxed = true)

    @Nested
    inner class InitializationTest {

        @Test
        fun `initialization cache`() {
            val mapper = DefaultDivisionRawMapper(defaultMappingType, divisionRepository)
            val divisions = listOf(createDivision(code = "division01"), createDivision(code = "division02"), createDivision(code = "division03"))

            every { divisionRepository.findByMappingType(defaultMappingType) } returns divisions
            mapper.afterPropertiesSet()

            assertThat(mapper.cache).isEqualTo(divisions)
            verify { divisionRepository.detached(mapper.cache) }
        }
    }

    @Nested
    inner class MappingTest {

        @ParameterizedTest
        @MethodSource(value = ["mappedRawAndDivisionCodeProvider"])
        fun `find division code by given raw tests`(givenRaws: List<String>, resultDivisionCodes: List<String>) {
            val mapper = DefaultDivisionRawMapper(defaultMappingType, divisionRepository)
            val divisions = listOf(
                createDivision(code = "divisionCode0", raws = setOf(createRaw("raw00000"), createRaw("raw00001"), createRaw("raw00002"))),
                createDivision(code = "divisionCode1", raws = setOf(createRaw("raw10000"), createRaw("raw10001"), createRaw("raw10002"))),
                createDivision(code = "divisionCode2", raws = setOf(createRaw("raw20000"), createRaw("raw20001"), createRaw("raw20002")))
            )

            every { divisionRepository.findByMappingType(defaultMappingType) } returns divisions
            mapper.afterPropertiesSet()

            val result = mapper.mapping(givenRaws)
            assertThat(result).isEqualTo(resultDivisionCodes)
        }

        private fun mappedRawAndDivisionCodeProvider() = Stream.of(
            Arguments.of(listOf("raw00000", "raw10000", "raw20000"), listOf("divisionCode0", "divisionCode1", "divisionCode2")),
            Arguments.of(listOf("raw00001", "raw10001", "raw20001"), listOf("divisionCode0", "divisionCode1", "divisionCode2")),
            Arguments.of(listOf("raw00002", "raw10002", "raw20002"), listOf("divisionCode0", "divisionCode1", "divisionCode2")),
            Arguments.of(listOf("raw00000", "raw10000"), listOf("divisionCode0", "divisionCode1")),
            Arguments.of(listOf("raw00000", "raw20000"), listOf("divisionCode0", "divisionCode2")),
            Arguments.of(listOf("raw10000", "raw20000"), listOf("divisionCode1", "divisionCode2")),
            Arguments.of(listOf("raw00000"), listOf("divisionCode0")),
            Arguments.of(listOf("raw10000"), listOf("divisionCode1")),
            Arguments.of(listOf("raw20000"), listOf("divisionCode2")),
        )
    }
}