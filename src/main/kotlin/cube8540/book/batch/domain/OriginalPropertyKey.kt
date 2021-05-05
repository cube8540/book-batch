package cube8540.book.batch.domain

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class OriginalPropertyKey(
    @Column(name = "property", length = 32)
    var property: String,

    @Column(name = "mapping_type", length = 32)
    @Enumerated(EnumType.STRING)
    var mappingType: MappingType
): Serializable