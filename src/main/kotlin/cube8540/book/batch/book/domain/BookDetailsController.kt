package cube8540.book.batch.book.domain

interface BookDetailsController {
    fun merge(base: BookDetails, item: BookDetails): BookDetails?
}