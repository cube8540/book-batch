package cube8540.book.batch.domain

import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class RawProperty(
    var raw: String,

    @Enumerated(EnumType.STRING)
    var mappingType: MappingType
): Serializable