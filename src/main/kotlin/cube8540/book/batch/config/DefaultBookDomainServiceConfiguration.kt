package cube8540.book.batch.config

import cube8540.book.batch.domain.MappingType
import cube8540.book.batch.domain.repository.BookOriginalFilterRepository
import cube8540.book.batch.domain.repository.DivisionRepository
import cube8540.book.batch.domain.repository.PublisherRepository
import cube8540.book.batch.infra.DefaultBookDetailsFilterFunction
import cube8540.book.batch.infra.DefaultDivisionRawMapper
import cube8540.book.batch.infra.DefaultPublisherRawMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DefaultBookDomainServiceConfiguration {

    @Bean
    fun naverPublisherRawMapper(repository: PublisherRepository) =
        DefaultPublisherRawMapper(MappingType.NAVER_BOOK, repository)

    @Bean
    fun nationalLibraryPublisherRawMapper(repository: PublisherRepository) =
        DefaultPublisherRawMapper(MappingType.NATIONAL_LIBRARY, repository)

    @Bean
    fun kyoboDivisionRawMapper(repository: DivisionRepository) =
        DefaultDivisionRawMapper(MappingType.KYOBO, repository)

    @Bean
    fun nationalLibraryFilterFunction(repository: BookOriginalFilterRepository) =
        DefaultBookDetailsFilterFunction(MappingType.NATIONAL_LIBRARY, repository)

    @Bean
    fun kyoboBookFilterFunction(repository: BookOriginalFilterRepository) =
        DefaultBookDetailsFilterFunction(MappingType.KYOBO, repository)
}
