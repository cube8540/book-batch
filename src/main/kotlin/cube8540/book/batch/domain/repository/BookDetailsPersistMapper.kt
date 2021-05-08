package cube8540.book.batch.domain.repository

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.domain.MappingType
import org.apache.ibatis.annotations.Mapper

@Mapper
interface BookDetailsPersistMapper {

    fun persistBookDetails(bookDetails: List<BookDetails>)

    fun mergeBookDetails(bookDetails: List<BookDetails>)

    fun persistDivisions(bookDetails: List<BookProperty>)

    fun deleteDivisions(bookDetails: List<BookDetails>)

    fun persistAuthors(bookDetails: List<BookProperty>)

    fun deleteAuthors(bookDetails: List<BookDetails>)

    fun persistKeywords(bookDetails: List<BookProperty>)

    fun deleteKeywords(bookDetails: List<BookDetails>)

    fun persistOriginals(bookDetails: List<BookOriginalProperty>)

    fun deleteOriginals(bookDetails: List<BookDetails>)

    fun updateForUpstreamTarget(bookDetails: List<BookDetails>)
}

data class BookProperty(val isbn: String, val value: String)

data class BookOriginalProperty(val isbn: String, val property: String, val mappingType: MappingType, val value: String?)