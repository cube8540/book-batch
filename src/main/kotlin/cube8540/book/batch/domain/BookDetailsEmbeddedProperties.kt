package cube8540.book.batch.domain

import cube8540.book.batch.domain.converter.RegexConverter
import cube8540.book.batch.domain.converter.ThumbnailConverter
import java.io.Serializable
import java.net.URI
import javax.persistence.*

@Embeddable
data class OriginalPropertyKey(
    @Column(name = "property", length = 32)
    var property: String,

    @Column(name = "mapping_type", length = 32)
    @Enumerated(EnumType.STRING)
    var mappingType: MappingType
): Serializable


@Embeddable
data class RawProperty(
    var raw: String,

    @Enumerated(EnumType.STRING)
    var mappingType: MappingType
): Serializable

@Embeddable
data class Thumbnail(
    @Convert(converter = ThumbnailConverter::class)
    @Column(name = "lage_thumbnail_url", length = 128)
    var largeThumbnail: URI?,

    @Convert(converter = ThumbnailConverter::class)
    @Column(name = "medium_thumbnail_url", length = 128)
    var mediumThumbnail: URI?,

    @Convert(converter = ThumbnailConverter::class)
    @Column(name = "small_thumbnail_url", length = 128)
    var smallThumbnail: URI?
): Serializable

@Embeddable
data class PropertyRegex(
    @Column(name = "property_name", length = 32)
    var propertyName: String,

    @Convert(converter = RegexConverter::class)
    @Column(name = "regex", length = 128)
    var regex: Regex
): Serializable