package cube8540.book.batch.infra

import cube8540.book.batch.domain.repository.DivisionCustomRepository
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.divisionCode0
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.divisionCode1
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.divisionCode2
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.divisions
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.mappingType
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.raw00
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.raw01
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.raw02
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.raw10
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.raw11
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.raw12
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.raw20
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.raw21
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.raw22
import cube8540.book.batch.infra.DefaultDivisionRawMapperTestEnvironment.reloadedDivisions
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class DefaultDivisionRawMapperTest {

    private val divisionRepository: DivisionCustomRepository = mockk()

    private val divisionRawMapper: DefaultDivisionRawMapper

    init {
        every { divisionRepository.findByMappingType(mappingType) } returns divisions
        divisionRawMapper = DefaultDivisionRawMapper(mappingType, divisionRepository)
    }

    @Nested
    inner class InitializationTest {

        @Test
        fun `initialization cache`() {
            assertThat(divisionRawMapper.catch).isEqualTo(divisions)
        }
    }

    @Nested
    inner class MappingTest {

        @ParameterizedTest
        @MethodSource(value = ["mappedRawAndDivisionCodeProvider"])
        fun `find division code by given raw tests`(givenRaws: List<String>, resultDivisionCodes: List<String>) {
            val result = divisionRawMapper.mapping(givenRaws)

            assertThat(result).isEqualTo(resultDivisionCodes)
        }

        private fun mappedRawAndDivisionCodeProvider() = Stream.of(
            Arguments.of(listOf(raw00, raw10, raw20), listOf(divisionCode0, divisionCode1, divisionCode2)),
            Arguments.of(listOf(raw01, raw11, raw21), listOf(divisionCode0, divisionCode1, divisionCode2)),
            Arguments.of(listOf(raw02, raw12, raw22), listOf(divisionCode0, divisionCode1, divisionCode2)),
            Arguments.of(listOf(raw00, raw10), listOf(divisionCode0, divisionCode1)),
            Arguments.of(listOf(raw00, raw20), listOf(divisionCode0, divisionCode2)),
            Arguments.of(listOf(raw10, raw20), listOf(divisionCode1, divisionCode2)),
            Arguments.of(listOf(raw00), listOf(divisionCode0)),
            Arguments.of(listOf(raw10), listOf(divisionCode1)),
            Arguments.of(listOf(raw20), listOf(divisionCode2)),
        )
    }

    @Nested
    inner class ReloadTest {

        @Test
        fun `reload division`() {
            every { divisionRepository.findByMappingType(mappingType) } returns reloadedDivisions

            divisionRawMapper.reload()
            assertThat(divisionRawMapper.catch).isEqualTo(reloadedDivisions)
        }
    }
}