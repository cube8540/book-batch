package cube8540.book.batch.domain

import javax.persistence.*

@Entity
@Table(name = "publishers")
class Publisher(
    @Id
    @Column(name = "publisher_code", length = 32)
    var code: String,

    @ElementCollection
    @CollectionTable(name = "publisher_raw_mappings", joinColumns = [JoinColumn(name = "publisher_code")])
    @AttributeOverrides(value = [
        AttributeOverride(name = "raw", column = Column(name = "raw", length = 32)),
        AttributeOverride(name = "mappingType", column = Column(name = "mapping_type", length = 32))
    ])
    // API 결과와 출판사 코드를 매칭하기 위해 사용
    var raws: Set<RawProperty>,

    @ElementCollection
    @CollectionTable(name = "publisher_keyword_mappings", joinColumns = [JoinColumn(name = "publisher_code")])
    @AttributeOverrides(value = [
        AttributeOverride(name = "raw", column = Column(name = "keyword", length = 32)),
        AttributeOverride(name = "mappingType", column = Column(name = "mapping_type", length = 32))
    ])
    // API 검색 키워드로 사용
    var keywords: Set<RawProperty>
) {

    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        other is Publisher && other.code == code -> true
        else -> false
    }

    override fun hashCode(): Int = code.hashCode()

}