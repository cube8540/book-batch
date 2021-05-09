package cube8540.book.batch.external.nl.go

import com.fasterxml.jackson.databind.JsonNode
import java.util.regex.Pattern

interface BookTitleExtractor {
    fun extract(jsonNode: JsonNode): String
}

class DefaultNationalLibraryAPITitleExtractor: BookTitleExtractor {

    companion object {
        private const val volumePattern = "\\s\\d+권?\$"
    }

    override fun extract(jsonNode: JsonNode): String {
        val volume = jsonNode.get(NationalLibraryAPIResponseNames.seriesNo)?.asInt()?: 0
        val originalTitle = jsonNode.get(NationalLibraryAPIResponseNames.title).asText()

        return when {
            volume != 0 -> {
                "${originalTitle.replace(Regex(volumePattern), "")} $volume"
            }
            Pattern.compile(volumePattern).matcher(originalTitle).find() -> {
                originalTitle.replace(Regex("권$"), "")
            }
            else -> {
                originalTitle
            }
        }
    }
}