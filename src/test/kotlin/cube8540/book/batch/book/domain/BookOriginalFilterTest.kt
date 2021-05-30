package cube8540.book.batch.book.domain

import io.github.cube8540.validator.core.Operator
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

class BookOriginalFilterTest {

    private val id: String = UUID.randomUUID().toString().replace("-", "")
    private val idGenerator: BookDetailsFilterIdGenerator = mockk(relaxed = true) {
        every { generate() } returns id
    }

    private val filter: BookOriginalFilter = BookOriginalFilter(idGenerator, MappingType.NAVER_BOOK)

    @ParameterizedTest
    @MethodSource(value = ["operationResultProvider"])
    fun `validation when filter is operator`(operatorType: Operator.OperatorType, resultLeft: Boolean, resultRight: Boolean, expected: Boolean) {
        val target: BookDetails = mockk(relaxed = true)
        val left: BookOriginalFilter = mockk(relaxed = true) {
            every { isValid(target) } returns resultLeft
        }
        val right: BookOriginalFilter = mockk(relaxed = true) {
            every { isValid(target) } returns resultRight
        }

        configOperator(operatorType, listOf(left, right))

        val result = filter.isValid(target)
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `validation when filter is node`() {
        val propertyName = "propertyName"
        val propertyValue = "propertyValue"
        val regex: Regex = mockk(relaxed = true) {
            every { matches(propertyValue) } returns true
        }

        val original = HashMap<OriginalPropertyKey, String?>()
        val target: BookDetails = mockk(relaxed = true)
        val propertyRegex: PropertyRegex = mockk(relaxed = true)

        original[OriginalPropertyKey(propertyName, MappingType.NAVER_BOOK)] = propertyValue
        every { propertyRegex.propertyName } returns propertyName
        every { propertyRegex.regex } returns regex
        configOperand(propertyRegex, MappingType.NAVER_BOOK)
        every { target.original } returns original

        val result = filter.isValid(target)
        assertThat(result).isTrue
    }

    private fun configOperator(operatorType: Operator.OperatorType, children: List<BookOriginalFilter>) {
        filter.root = true
        filter.propertyRegex = null
        filter.operatorType = operatorType
        filter.children = children.toMutableList()
    }

    private fun configOperand(propertyRegex: PropertyRegex, mappingType: MappingType) {
        filter.root = false
        filter.operatorType = null
        filter.children = null
        filter.mappingType = mappingType
        filter.propertyRegex = propertyRegex
    }

    private fun operationResultProvider() = Stream.of(
        Arguments.of(Operator.OperatorType.AND, true, true, true),
        Arguments.of(Operator.OperatorType.AND, true, false, false),
        Arguments.of(Operator.OperatorType.AND, false, false, false),

        Arguments.of(Operator.OperatorType.OR, true, true, true),
        Arguments.of(Operator.OperatorType.OR, true, false, true),
        Arguments.of(Operator.OperatorType.OR, false, false, false),

        Arguments.of(Operator.OperatorType.NAND, true, true, false),
        Arguments.of(Operator.OperatorType.NAND, true, false, true),
        Arguments.of(Operator.OperatorType.NAND, false, false, true),

        Arguments.of(Operator.OperatorType.NOR, true, true, false),
        Arguments.of(Operator.OperatorType.NOR, true, false, false),
        Arguments.of(Operator.OperatorType.NOR, false, false, true)
    )
}