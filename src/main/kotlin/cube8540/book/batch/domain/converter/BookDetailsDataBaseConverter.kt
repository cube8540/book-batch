package cube8540.book.batch.domain.converter

import java.net.URI
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class ThumbnailConverter: AttributeConverter<URI?, String?> {
    override fun convertToDatabaseColumn(attribute: URI?): String? = attribute?.toString()

    override fun convertToEntityAttribute(dbData: String?): URI? = dbData?.let { it -> URI.create(it) }
}