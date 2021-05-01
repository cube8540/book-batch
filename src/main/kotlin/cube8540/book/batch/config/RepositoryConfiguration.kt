package cube8540.book.batch.config

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Configuration
class RepositoryConfiguration(
    @PersistenceContext
    val entityManager: EntityManager
) {

    @Bean
    fun jpaQueryFactoryBean(): JPAQueryFactory = JPAQueryFactory(entityManager)
}