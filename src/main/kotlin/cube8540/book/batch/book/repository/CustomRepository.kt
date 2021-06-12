package cube8540.book.batch.book.repository

interface CustomRepository<in D> {
    fun detached(entity: D)

    fun detached(entities: Collection<D>)
}