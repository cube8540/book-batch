package cube8540.book.batch.book.infra

import cube8540.book.batch.book.domain.BookDetails
import cube8540.book.batch.external.BookDetailsController

class CompositeBookDetailsController(private vararg val delegators: BookDetailsController): BookDetailsController {

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