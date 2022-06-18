package cube8540.book.batch.translator.nl.go

import cube8540.book.batch.book.application.DefaultBookCommandService
import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.infra.DefaultBookDetailsFilterFunction
import cube8540.book.batch.book.infra.DefaultPublisherRawMapper
import cube8540.book.batch.book.repository.BookDetailsRepository
import cube8540.book.batch.book.repository.BookOriginalFilterRepository
import cube8540.book.batch.book.repository.PublisherRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NationalLibraryBeanConfiguration {

    @Bean
    fun nationalLibraryPublisherRawMapper(repository: PublisherRepository) =
        DefaultPublisherRawMapper(MappingType.NATIONAL_LIBRARY, repository)

    @Bean
    fun nationalLibraryFilterFunction(repository: BookOriginalFilterRepository) =
        DefaultBookDetailsFilterFunction(MappingType.NATIONAL_LIBRARY, repository)

    @Bean
    fun nationalLibraryBookCommandService(repository: BookDetailsRepository) =
        DefaultBookCommandService(repository, NationalLibraryBookDetailsController())
}