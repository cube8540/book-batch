package cube8540.book.batch.book.domain

import cube8540.book.batch.book.converter.RegexConverter
import cube8540.book.batch.book.converter.URIConverter
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
    @Convert(converter = URIConverter::class)
    @Column(name = "lage_thumbnail_url", length = 128)
    var largeThumbnail: URI? = null,

    @Convert(converter = URIConverter::class)
    @Column(name = "medium_thumbnail_url", length = 128)
    var mediumThumbnail: URI? = null,

    @Convert(converter = URIConverter::class)
    @Column(name = "small_thumbnail_url", length = 128)
    var smallThumbnail: URI? = null
): Serializable

@Embeddable
data class PropertyRegex(
    @Column(name = "property_name", length = 32)
    var propertyName: String,

    @Convert(converter = RegexConverter::class)
    @Column(name = "regex", length = 128)
    var regex: Regex
): Serializable

@Embeddable
data class BookExternalLink(
    @Column(name = "product_detail_page", length = 248, nullable = false)
    @Convert(converter = URIConverter::class)
    var productDetailPage: URI,

    @Column(name = "original_price", nullable = true)
    var originalPrice: Double? = null,

    @Column(name = "sale_price", nullable = true)
    var salePrice: Double? = null
): Serializable

@Embeddable
data class UpstreamFailedReason(
    var property: String,

    var message: String
): Serializable