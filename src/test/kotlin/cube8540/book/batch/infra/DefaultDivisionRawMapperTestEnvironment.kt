package cube8540.book.batch.infra

import cube8540.book.batch.domain.Division
import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.RawProperty
import io.mockk.mockk

object DefaultDivisionRawMapperTestEnvironment {

    internal const val divisionCode0 = "division0000"
    internal const val divisionCode1 = "division0001"
    internal const val divisionCode2 = "division0002"

    internal const val raw00 = "raw00000"
    internal const val raw01 = "raw00001"
    internal const val raw02 = "raw00002"

    internal const val raw10 = "raw10000"
    internal const val raw11 = "raw10001"
    internal const val raw12 = "raw10002"

    internal const val raw20 = "raw20000"
    internal const val raw21 = "raw20001"
    internal const val raw22 = "raw20002"

    internal val mappingType: MappingType = MappingType.NAVER_BOOK

    private val division0 = Division(
        code = divisionCode0,
        raws = setOf(RawProperty(raw00, mappingType), RawProperty(raw01, mappingType), RawProperty(raw02, mappingType)),
        depth = 0
    )

    private val division1 = Division(
        code = divisionCode1,
        raws = setOf(RawProperty(raw10, mappingType), RawProperty(raw11, mappingType), RawProperty(raw12, mappingType)),
        depth = 0
    )

    private val division2 = Division(
        code = divisionCode2,
        raws = setOf(RawProperty(raw20, mappingType), RawProperty(raw21, mappingType), RawProperty(raw22, mappingType)),
        depth = 0
    )
    internal val divisions = listOf(division0, division1, division2)

    internal val reloadedDivisions: List<Division> = listOf(mockk(), mockk(), mockk())

}