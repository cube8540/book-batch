package cube8540.book.batch.translator.naver.com

import cube8540.book.batch.book.application.DefaultBookCommandService
import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.infra.DefaultBookDetailsFilterFunction
import cube8540.book.batch.book.infra.DefaultPublisherRawMapper
import cube8540.book.batch.book.repository.BookDetailsRepository
import cube8540.book.batch.book.repository.BookOriginalFilterRepository
import cube8540.book.batch.book.repository.PublisherRepository
import cube8540.book.batch.translator.naver.com.application.NaverBookDetailsController
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
    fun naverBookAPICommandService(repository: BookDetailsRepository) =
        DefaultBookCommandService(repository, NaverBookDetailsController())
}