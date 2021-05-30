package cube8540.book.batch.book

import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.repository.BookOriginalFilterRepository
import cube8540.book.batch.book.repository.DivisionRepository
import cube8540.book.batch.book.repository.PublisherRepository
import cube8540.book.batch.book.infra.DefaultBookDetailsFilterFunction
import cube8540.book.batch.book.infra.DefaultDivisionRawMapper
import cube8540.book.batch.book.infra.DefaultPublisherRawMapper
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
    fun naverBookAPIFilterFunction(repository: BookOriginalFilterRepository) =
        DefaultBookDetailsFilterFunction(MappingType.NAVER_BOOK, repository)

    @Bean
    fun kyoboBookFilterFunction(repository: BookOriginalFilterRepository) =
        DefaultBookDetailsFilterFunction(MappingType.KYOBO, repository)
}
