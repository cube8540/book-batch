package cube8540.book.batch.infra

import cube8540.book.batch.domain.BookDetails
import cube8540.book.batch.external.BookDetailsController

class CompositeBookDetailsController(private val delegators: List<BookDetailsController>): BookDetailsController {

    override fun merge(base: BookDetails, item: BookDetails): BookDetails? = when (delegators.isEmpty()) {
        true -> base
        else -> merge(base, item, delegators.iterator())
    }

    private fun merge(base: BookDetails, item: BookDetails, controllers: Iterator<BookDetailsController>): BookDetails? {
        val controller = controllers.next()
        val result = controller.merge(base, item)

        return if (result == null || !controllers.hasNext()) {
            result
        } else {
            merge(result, item, controllers)
        }
    }
}