package cube8540.book.batch.external.naver.com

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.domain.PublisherRawMapper
import cube8540.book.batch.book.infra.DefaultBookDetailsFilterFunction
import cube8540.book.batch.book.infra.DefaultPublisherRawMapper
import cube8540.book.batch.book.repository.BookOriginalFilterRepository
import cube8540.book.batch.book.repository.PublisherRepository
import cube8540.book.batch.external.BookAPIErrorResponse
import cube8540.book.batch.external.BookAPIResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NaverBookBeanConfiguration {

    @Bean
    fun naverPublisherRawMapper(repository: PublisherRepository) =
        DefaultPublisherRawMapper(MappingType.NAVER_BOOK, repository)

    @Bean
    fun naverBookAPIFilterFunction(repository: BookOriginalFilterRepository) =
        DefaultBookDetailsFilterFunction(MappingType.NAVER_BOOK, repository)

    @Bean
    fun naverBookAPIObjectMapper(
        @Qualifier("naverPublisherRawMapper") publisherRawMapper: PublisherRawMapper
    ): ObjectMapper = ObjectMapper()
        .registerModule(
            SimpleModule()
                .addDeserializer(BookAPIResponse::class.java, NaverBookAPIDeserializer(publisherRawMapper))
                .addDeserializer(BookAPIErrorResponse::class.java, NaverBookAPIErrorDeserializer())
        )
}