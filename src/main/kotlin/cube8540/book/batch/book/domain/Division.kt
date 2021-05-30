package cube8540.book.batch.book.domain

import javax.persistence.*

@Entity
@Table(name = "divisions")
class Division(
    @Id
    @Column(name = "division_code")
    var code: String,

    @ElementCollection
    @CollectionTable(name = "division_raw_mappings", joinColumns = [JoinColumn(name = "division_code")])
    @AttributeOverrides(value = [
        AttributeOverride(name = "raw", column = Column(name = "raw", length = 32)),
        AttributeOverride(name = "mappingType", column = Column(name = "mapping_type", length = 32))
    ])
    // API 결과와 분류 코드를 매칭 시킬때 사용
    var raws: Set<RawProperty>,

    @Column(name = "depth", nullable = false)
    var depth: Int
) {
    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        other is Division && other.code == code -> true
        else -> false
    }

    override fun hashCode(): Int = code.hashCode()
}