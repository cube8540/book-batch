package cube8540.book.batch.translator.aladin.kr

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
class AladinBookBeanConfiguration {

    @Bean
    fun aladinPublisherRawMapper(repository: PublisherRepository) =
        DefaultPublisherRawMapper(MappingType.ALADIN, repository)

    @Bean
    fun aladinAPIFilterFunction(repository: BookOriginalFilterRepository) =
        DefaultBookDetailsFilterFunction(MappingType.ALADIN, repository)

    @Bean
    fun aladinAPICommandService(repository: BookDetailsRepository) =
        DefaultBookCommandService(repository, AladinBookDetailsController())

}