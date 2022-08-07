package cube8540.book.batch.translator.store.application

import cube8540.book.batch.book.application.DefaultBookCommandService
import cube8540.book.batch.book.repository.BookDetailsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ExternalApplicationBeanConfiguration {

    @Bean
    fun externalApplicationBookCommandService(repository: BookDetailsRepository) =
        DefaultBookCommandService(repository, EmptyBookDetailsController())
}