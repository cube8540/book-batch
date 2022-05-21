package cube8540.book.batch.translator

interface PageDecision {
    fun calculation(page: Int, pageSize: Int): Int
}

class DefaultPageDecision: PageDecision {
    override fun calculation(page: Int, pageSize: Int): Int = page
}