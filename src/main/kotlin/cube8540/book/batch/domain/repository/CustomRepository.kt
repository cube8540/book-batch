package cube8540.book.batch.domain.repository

interface CustomRepository<in D> {
    fun detached(entity: D)

    fun detached(entities: Collection<D>)
}