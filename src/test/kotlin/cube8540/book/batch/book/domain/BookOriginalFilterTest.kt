package cube8540.book.batch.book.domain

import io.github.cube8540.validator.core.Operator
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class BookOriginalFilterTest {

    @ParameterizedTest
    @MethodSource(value = ["operationResultProvider"])
    fun `validation when filter is operator`(operatorType: Operator.OperatorType, resultLeft: Boolean, resultRight: Boolean, expected: Boolean) {
        val target: BookDetails = createBookDetails()
        val left: BookOriginalFilter = mockk(relaxed = true) {
            every { isValid(target) } returns resultLeft
        }
        val right: BookOriginalFilter = mockk(relaxed = true) {
            every { isValid(target) } returns resultRight
        }

        val filter = createBookFilterOperator(operatorType = operatorType, children = listOf(left, right))

        val result = filter.isValid(target)
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `validation when filter is node`() {
        val original = HashMap<OriginalPropertyKey, String>()
        original[OriginalPropertyKey("property", defaultBookOriginalMappingType)] = "value"

        val target: BookDetails = createBookDetails(original = original)
        val propertyRegex = PropertyRegex("property", mockk(relaxed = true) {
            every { matches("value") } returns true
        })

        val filter = createBookFilterOperand(propertyRegex = propertyRegex)

        val result = filter.isValid(target)
        assertThat(result).isTrue
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