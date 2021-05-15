package cube8540.book.batch.domain

import io.github.cube8540.validator.core.Operator
import io.github.cube8540.validator.core.Validatable
import javax.persistence.*

@Entity
@Table(name = "book_original_filters")
class BookOriginalFilter(idGenerator: BookDetailsFilterIdGenerator, mappingType: MappingType): Operator<BookDetails> {

    @Id
    @Column(name = "id", length = 32)
    var id: String? = idGenerator.generate()

    @Column(name = "name", length = 32)
    var name: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "mapping_type", length = 32)
    var mappingType: MappingType? = mappingType

    @Column(name = "is_root")
    var root: Boolean? = true

    @Enumerated(EnumType.STRING)
    @Column(name = "operator_type", length = 32)
    var operatorType: Operator.OperatorType? = null

    @Embedded
    var propertyRegex: PropertyRegex? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: BookOriginalFilter? = null

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL])
    var children: MutableList<BookOriginalFilter>? = null

    override fun isValid(target: BookDetails): Boolean {
        return if (root == true) {
            if (operatorType == Operator.OperatorType.AND || operatorType == Operator.OperatorType.NAND) {
                operatorType == Operator.OperatorType.AND == and(target)
            } else {
                operatorType == Operator.OperatorType.OR == or(target)
            }
        } else {
            target.original?.get(OriginalPropertyKey(propertyRegex!!.propertyName, mappingType!!))
                ?.let { propertyRegex!!.regex.matches(it) } ?: false
        }
    }

    override fun getType(): Operator.OperatorType? = operatorType

    override fun getOperands(): List<Validatable<BookDetails>>? = children?.toList()

    override fun equals(other: Any?): Boolean = when (other) {
        null -> {
            false
        }
        is BookOriginalFilter -> {
            other.id == id
        }
        else -> {
            false
        }
    }

    override fun hashCode(): Int = id.hashCode()

    private fun and(bookDetails: BookDetails): Boolean = children!!.stream().allMatch { it.isValid(bookDetails) }

    private fun or(bookDetails: BookDetails): Boolean = children!!.stream().anyMatch { it.isValid(bookDetails) }
}