package cube8540.book.batch.external.aladin.kr

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import cube8540.book.batch.book.application.DefaultBookCommandService
import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.book.infra.DefaultBookDetailsFilterFunction
import cube8540.book.batch.book.infra.DefaultPublisherRawMapper
import cube8540.book.batch.book.repository.BookDetailsRepository
import cube8540.book.batch.book.repository.BookOriginalFilterRepository
import cube8540.book.batch.book.repository.PublisherRepository
import cube8540.book.batch.interlock.BookAPIResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AladinBookBeanConfiguration {

    @Bean
    fun aladinPublisherRawMapper(repository: PublisherRepository) =
        DefaultPublisherRawMapper(MappingType.ALADIN, repository)

    @Bean
    fun aladinAPIObjectMapper(
        @Qualifier("aladinPublisherRawMapper") publisherRawMapper: PublisherRawMapper
    ): ObjectMapper = ObjectMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(BookAPIResponse::class.java, AladinAPIDeserializer(publisherRawMapper))
        )

    @Bean
    fun aladinAPIFilterFunction(repository: BookOriginalFilterRepository) =
        DefaultBookDetailsFilterFunction(MappingType.ALADIN, repository)

    @Bean
    fun aladinAPICommandService(repository: BookDetailsRepository) =
        DefaultBookCommandService(repository, AladinBookDetailsController())

}