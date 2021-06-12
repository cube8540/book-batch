package cube8540.book.batch

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDateTime

fun URI.getQueryParams(): Map<String, List<String?>?> {
    val queryPairs = LinkedHashMap<String, MutableList<String?>?>()

    this.query.split("&").forEach {
        val index = it.indexOf("=")
        val key = when (index > 0) {
            true -> URLDecoder.decode(it.substring(0, index), StandardCharsets.UTF_8)
            else -> it
        }
        val value = when (index > 0 && it.length > index + 1) {
            true -> URLDecoder.decode(it.substring(index + 1), StandardCharsets.UTF_8)
            else -> null
        }
        if (queryPairs[key] == null) {
            queryPairs[key] = ArrayList()
        }
        queryPairs[key]!!.add(value)
    }

    return queryPairs
}

fun LocalDateTime.toDefaultInstance(): Instant = this.toInstant(BatchApplication.DEFAULT_ZONE_OFFSET)