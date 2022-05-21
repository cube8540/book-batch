package cube8540.book.batch.external.nl.go

import com.fasterxml.jackson.databind.DeserializationFeature
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
import cube8540.book.batch.translator.BookAPIErrorResponse
import cube8540.book.batch.translator.BookAPIResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NationalLibraryBeanConfiguration {

    @Bean
    fun nationalLibraryObjectMapper(
        @Qualifier("nationalLibraryPublisherRawMapper") publisherRawMapper: PublisherRawMapper
    ): ObjectMapper = ObjectMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(BookAPIResponse::class.java, NationalLibraryAPIDeserializer(publisherRawMapper))
                .addDeserializer(BookAPIErrorResponse::class.java, NationalLibraryAPIErrorDeserializer())
        )
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)

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