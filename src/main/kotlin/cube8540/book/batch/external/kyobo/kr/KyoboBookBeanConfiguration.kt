package cube8540.book.batch.external.kyobo.kr

import cube8540.book.batch.book.application.DefaultBookCommandService
import cube8540.book.batch.book.domain.DivisionRawMapper
import cube8540.book.batch.book.domain.MappingType
import cube8540.book.batch.book.infra.DefaultBookDetailsFilterFunction
import cube8540.book.batch.book.infra.DefaultDivisionRawMapper
import cube8540.book.batch.book.repository.BookDetailsRepository
import cube8540.book.batch.book.repository.BookOriginalFilterRepository
import cube8540.book.batch.book.repository.DivisionRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KyoboBookBeanConfiguration {

    @Bean
    fun kyoboBookDocumentMapper(
        @Qualifier("kyoboDivisionRawMapper") divisionRawMapper: DivisionRawMapper
    ) = KyoboBookDocumentMapper(divisionRawMapper)

    @Bean
    fun kyoboDivisionRawMapper(repository: DivisionRepository) =
        DefaultDivisionRawMapper(MappingType.KYOBO, repository)

    @Bean
    fun kyoboBookFilterFunction(repository: BookOriginalFilterRepository) =
        DefaultBookDetailsFilterFunction(MappingType.KYOBO, repository)

    @Bean
    fun kyoboBookCommandService(repository: BookDetailsRepository) =
        DefaultBookCommandService(repository, KyoboBookDetailsController())
}